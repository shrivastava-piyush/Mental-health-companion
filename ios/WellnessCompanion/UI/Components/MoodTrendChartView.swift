import SwiftUI

struct MoodTrendChartView: View {
    let buckets: [DailyMoodBucket]

    var body: some View {
        GeometryReader { geo in
            let w = geo.size.width
            let h = geo.size.height
            let count = buckets.count
            
            if count < 2 {
                VStack {
                    Spacer()
                    Text("Need more data for patterns")
                        .font(.system(.caption, design: .rounded))
                        .fontWeight(.bold)
                        .foregroundStyle(Color.wellnessSecondaryText.opacity(0.4))
                    Spacer()
                }
                .frame(maxWidth: CGFloat.infinity)
            } else {
                let stepX = w / CGFloat(count - 1)
                let points = buckets.enumerated().map { i, b in
                    CGPoint(
                        x: CGFloat(i) * stepX,
                        // Mapping valence -100...100 (from turn 33/38 logic) to height
                        y: (h * 0.8) - CGFloat(Double(b.avgValence) + 100) / 200 * (h * 0.6)
                    )
                }

                ZStack {
                    // Minimalist Grid
                    VStack {
                        Divider().opacity(0.05)
                        Spacer()
                        Divider().opacity(0.05)
                    }
                    .padding(.vertical, h * 0.1)

                    // Area fill with soft gradient
                    Path { path in
                        path.move(to: CGPoint(x: points[0].x, y: h))
                        path.addLine(to: points[0])
                        for pt in points.dropFirst() { path.addLine(to: pt) }
                        path.addLine(to: CGPoint(x: points.last!.x, y: h))
                        path.closeSubpath()
                    }
                    .fill(
                        LinearGradient(
                            colors: [Color.wellnessAccent.opacity(0.2), Color.wellnessAccent.opacity(0)],
                            startPoint: .top,
                            endPoint: .bottom
                        )
                    )

                    // Main trend line
                    Path { path in
                        path.move(to: points[0])
                        for pt in points.dropFirst() {
                            path.addLine(to: pt)
                        }
                    }
                    .stroke(
                        Color.wellnessAccent, 
                        style: StrokeStyle(lineWidth: 4, lineCap: .round, lineJoin: .round)
                    )

                    // Glowing key points
                    ForEach(Array(points.enumerated()), id: \.offset) { i, pt in
                        ZStack {
                            Circle()
                                .fill(Color.wellnessAccent.opacity(0.15))
                                .frame(width: 14, height: 14)
                            Circle()
                                .fill(Color.wellnessAccent)
                                .frame(width: 6, height: 6)
                        }
                        .position(pt)
                    }
                }
            }
        }
    }
}
