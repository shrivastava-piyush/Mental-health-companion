import SwiftUI
import PhotosUI

/// Manages the personalized background image selected by the user.
final class BackgroundManager: ObservableObject {
    @Published var selectedImage: UIImage? = nil
    
    init() {
        loadStoredImage()
    }
    
    func loadStoredImage() {
        let url = getSavedImageURL()
        if let data = try? Data(contentsOf: url) {
            selectedImage = UIImage(data: data)
        }
    }
    
    func saveImage(_ image: UIImage) {
        self.selectedImage = image
        if let data = image.jpegData(compressionQuality: 0.8) {
            try? data.write(to: getSavedImageURL())
        }
    }
    
    private func getSavedImageURL() -> URL {
        let docs = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
        return docs.appendingPathComponent("custom_background.jpg")
    }
}

/// A high-fidelity "Liquid Glass" background with a rhythmic breathing motion.
/// Features a "Memory Layer" that renders a faded, blurred user-selected image.
struct LiquidAura: View {
    @EnvironmentObject private var bgManager: BackgroundManager
    @State private var start = Date()
    var scrollOffset: CGFloat = 0
    
    private let breathingPeriod: Double = 8.0
    
    var body: some View {
        TimelineView(.animation) { timeline in
            let elapsed = timeline.date.timeIntervalSince(start)
            let breath = (sin(elapsed * .pi * 2 / breathingPeriod) + 1) / 2
            
            ZStack {
                // 1. Deep Space Base
                Color.liquidDeep
                
                // 2. The Memory Layer (User Photo)
                if let image = bgManager.selectedImage {
                    Image(uiImage: image)
                        .resizable()
                        .aspectRatio(contentMode: .fill)
                        .frame(maxWidth: .infinity, maxHeight: .infinity)
                        .blur(radius: 50)
                        .opacity(0.15 + (breath * 0.05)) // Faded breathing intensity
                        .scaleEffect(1.1 + (breath * 0.05)) // Subtle zoom pulse
                        .ignoresSafeArea()
                }
                
                // 3. The "Breathing" Fluid Layer
                GeometryReader { proxy in
                    let size = proxy.size
                    let w = size.width
                    let h = size.height
                    
                    ZStack {
                        blob(color: .liquidIndigo, size: w * 1.5, time: elapsed, speed: 0.2, phase: 0, center: CGPoint(x: w * 0.2, y: h * 0.3))
                        blob(color: .liquidTeal, size: w * 1.3, time: elapsed, speed: 0.15, phase: 2.5, center: CGPoint(x: w * 0.8, y: h * 0.6))
                        blob(color: .liquidRose, size: w * 1.4, time: elapsed, speed: 0.1, phase: 4.2, center: CGPoint(x: w * 0.3, y: h * 0.9))
                    }
                    .blur(radius: 80)
                    .scaleEffect(1.0 + (breath * 0.1))
                    .opacity(0.4 + (breath * 0.3))
                }
                
                // 4. Cinematic Vignette
                RadialGradient(colors: [.clear, .black.opacity(0.6)], center: .center, startRadius: 100, endRadius: 1200)
            }
            .ignoresSafeArea()
        }
    }
    
    @ViewBuilder
    private func blob(color: Color, size: CGFloat, time: Double, speed: Double, phase: Double, center: CGPoint) -> some View {
        let xOff = sin(time * speed + phase) * 120
        let yOff = cos(time * speed * 0.7 + phase) * 100
        
        Circle()
            .fill(color)
            .frame(width: size, height: size)
            .position(x: center.x + xOff, y: center.y + yOff + (scrollOffset * 0.05))
            .scaleEffect(0.9 + sin(time * speed * 0.4) * 0.2)
    }
}
