import SwiftUI

struct LiquidAura: View {
    @State private var start = Date()
    var scrollOffset: CGFloat = 0
    
    var body: some View {
        TimelineView(.animation) { timeline in
            let time = timeline.date.timeIntervalSince(start)
            
            Canvas { context, size in
                let w = size.width
                let h = size.height
                
                // Base deep layer
                context.fill(Path(CGRect(origin: .zero, size: size)), with: .color(.liquidDeep))
                
                // Flowing Liquid Blobs
                func drawLiquid(color: Color, offset: CGPoint, radius: CGFloat, speed: Double, phaseOffset: Double) {
                    let moveX = sin(time * speed + phaseOffset) * 60
                    let moveY = cos(time * speed * 0.7 + phaseOffset) * 40 + (scrollOffset * 0.1)
                    
                    let rect = CGRect(
                        x: offset.x + moveX - radius/2,
                        y: offset.y + moveY - radius/2,
                        width: radius,
                        height: radius
                    )
                    context.addFilter(.blur(radius: 80))
                    context.fill(Path(ellipseIn: rect), with: .color(color.opacity(0.4)))
                }
                
                drawLiquid(color: .liquidIndigo, offset: CGPoint(x: w * 0.2, y: h * 0.3), radius: 500, speed: 0.3, phaseOffset: 0)
                drawLiquid(color: .liquidTeal, offset: CGPoint(x: w * 0.8, y: h * 0.6), radius: 450, speed: 0.2, phaseOffset: 2.0)
                drawLiquid(color: .liquidRose, offset: CGPoint(x: w * 0.3, y: h * 0.9), radius: 400, speed: 0.15, phaseOffset: 4.0)
                drawLiquid(color: .liquidAmber, offset: CGPoint(x: w * 0.7, y: h * 0.1), radius: 350, speed: 0.25, phaseOffset: 1.0)
            }
            .overlay(
                // Film Grain Texture
                Rectangle()
                    .fill(.black.opacity(0.02))
                    .overlay(
                        Image(systemName: "circle.fill")
                            .resizable()
                            .scaledToFill()
                            .opacity(0.01)
                            .blendMode(.overlay)
                    )
            )
        }
    }
}
