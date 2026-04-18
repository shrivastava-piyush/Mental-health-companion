import SwiftUI

struct MoodTrendChartView: View {
    let buckets: [DailyMoodBucket]

    var body: some View {
        GeometryReader { geo in
            let w = geo.size.width
            let h = geo.size.height
            let count = buckets.count
            guard count >= 2 else {
                return AnyView(Text("Not enough data").font(.caption).foregroundStyle(.secondary))
            }

            let stepX = w / CGFloat(count - 1)
            let points = buckets.enumerated().map { i, b in
                CGPoint(
                    x: CGFloat(i) * stepX,
                    y: h / 2 - CGFloat(b.avgValence) * (h / 4)
                )
            }

            return AnyView(
                ZStack {
                    Path { path in
                        path.move(to: points[0])
                        for pt in points.dropFirst() { path.addLine(to: pt) }
                    }
                    .stroke(Color.sageGreen, lineWidth: 2)

                    Path { path in
                        path.move(to: CGPoint(x: points[0].x, y: h))
                        path.addLine(to: points[0])
                        for pt in points.dropFirst() { path.addLine(to: pt) }
                        path.addLine(to: CGPoint(x: points.last!.x, y: h))
                        path.closeSubpath()
                    }
                    .fill(Color.sageGreen.opacity(0.15))

                    ForEach(Array(points.enumerated()), id: \.offset) { _, pt in
                        Circle()
                            .fill(Color.sageGreen)
                            .frame(width: 6, height: 6)
                            .position(pt)
                    }
                }
            )
        }
    }
}
