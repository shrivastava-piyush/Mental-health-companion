import SwiftUI

/// A high-fidelity "Liquid Glass" background with a rhythmic breathing motion.
/// Uses TimelineView for guaranteed continuous animation and complex organic offsets.
struct LiquidAura: View {
    @State private var start = Date()
    var scrollOffset: CGFloat = 0
    
    // Breathing parameters
    private let breathingPeriod: Double = 8.0 // 8 seconds per breath
    private let motionSpeed: Double = 0.4
    
    var body: some View {
        TimelineView(.animation) { timeline in
            let elapsed = timeline.date.timeIntervalSince(start)
            
            // Breathing cycle (0.0 to 1.0)
            let breath = (sin(elapsed * .pi * 2 / breathingPeriod) + 1) / 2
            
            GeometryReader { proxy in
                let size = proxy.size
                let w = size.width
                let h = size.height
                
                ZStack {
                    // 1. Deep Space Base
                    Color.liquidDeep
                    
                    // 2. The "Breathing" Fluid Layer
                    ZStack {
                        // Large, melting blobs with high displacement
                        blob(color: .liquidIndigo, 
                             size: w * 1.5, 
                             time: elapsed, 
                             speed: 0.2, 
                             phase: 0, 
                             center: CGPoint(x: w * 0.2, y: h * 0.3))
                        
                        blob(color: .liquidTeal, 
                             size: w * 1.3, 
                             time: elapsed, 
                             speed: 0.15, 
                             phase: 2.5, 
                             center: CGPoint(x: w * 0.8, y: h * 0.6))
                        
                        blob(color: .liquidRose, 
                             size: w * 1.4, 
                             time: elapsed, 
                             speed: 0.1, 
                             phase: 4.2, 
                             center: CGPoint(x: w * 0.3, y: h * 0.9))
                        
                        blob(color: .liquidAmber, 
                             size: w * 1.2, 
                             time: elapsed, 
                             speed: 0.25, 
                             phase: 1.1, 
                             center: CGPoint(x: w * 0.7, y: h * 0.1))
                    }
                    .blur(radius: 80)
                    .scaleEffect(1.0 + (breath * 0.15)) // Global breathing expansion
                    .opacity(0.6 + (breath * 0.4))     // Global breathing intensity
                    
                    // 3. High-frequency detail (floating motes)
                    detailLayer(time: elapsed, size: size)
                    
                    // 4. Cinematic Vignette
                    RadialGradient(colors: [.clear, .black.opacity(0.5)], 
                                  center: .center, 
                                  startRadius: 100, 
                                  endRadius: 1200)
                }
                .ignoresSafeArea()
            }
        }
    }
    
    @ViewBuilder
    private func blob(color: Color, size: CGFloat, time: Double, speed: Double, phase: Double, center: CGPoint) -> some View {
        // Complex Lissajous motion
        let xOff = sin(time * speed + phase) * 120 + cos(time * speed * 0.5) * 60
        let yOff = cos(time * speed * 0.7 + phase) * 100 + sin(time * speed * 0.3) * 50
        
        Circle()
            .fill(color)
            .frame(width: size, height: size)
            .position(x: center.x + xOff, y: center.y + yOff + (scrollOffset * 0.05))
            .scaleEffect(0.9 + sin(time * speed * 0.4) * 0.2)
    }
    
    @ViewBuilder
    private func detailLayer(time: Double, size: CGSize) -> some View {
        Canvas { context, size in
            for i in 0..<10 {
                let x = (sin(time * 0.2 + Double(i)) + 1) / 2 * size.width
                let y = (cos(time * 0.15 + Double(i) * 0.5) + 1) / 2 * size.height
                let s = CGFloat.random(in: 2...4)
                context.fill(Path(ellipseIn: CGRect(x: x, y: y, width: s, height: s)), with: .color(.white.opacity(0.1)))
            }
        }
        .blendMode(.plusLighter)
    }
}
