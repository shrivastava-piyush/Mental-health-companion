import SwiftUI
import PhotosUI

/// Manages the personalized background image selected by the user.
/// Optimized for asynchronous loading to prevent startup hangs.
final class BackgroundManager: ObservableObject {
    @Published var selectedImage: UIImage? = nil
    
    init() {
        // Asynchronous load to ensure main thread remains responsive on startup
        Task {
            await loadStoredImage()
        }
    }
    
    @MainActor
    func loadStoredImage() async {
        let url = getSavedImageURL()
        
        // Load in background thread
        let image = await Task.detached(priority: .userInitiated) { () -> UIImage? in
            guard let data = try? Data(contentsOf: url) else { return nil }
            return UIImage(data: data)
        }.value
        
        self.selectedImage = image
    }
    
    @MainActor
    func saveImage(_ image: UIImage) {
        self.selectedImage = image
        Task.detached(priority: .background) { [url = getSavedImageURL()] in
            if let data = image.jpegData(compressionQuality: 0.8) {
                try? data.write(to: url)
            }
        }
    }
    
    private func getSavedImageURL() -> URL {
        let docs = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
        return docs.appendingPathComponent("custom_background.jpg")
    }
}

/// A high-fidelity "Timely-inspired" background with fluid gradients and rising bubbles.
struct LiquidAura: View {
    @EnvironmentObject private var bgManager: BackgroundManager
    @State private var start = Date()
    var scrollOffset: CGFloat = 0
    
    private let bubbleCount = 12
    @State private var bubblePositions: [CGPoint] = (0..<12).map { _ in CGPoint(x: CGFloat.random(in: 0...1), y: CGFloat.random(in: 0...1)) }
    @State private var bubbleSizes: [CGFloat] = (0..<12).map { _ in CGFloat.random(in: 20...100) }
    @State private var bubbleSpeeds: [Double] = (0..<12).map { _ in Double.random(in: 0.05...0.15) }
    
    var body: some View {
        TimelineView(.animation) { timeline in
            let elapsed = timeline.date.timeIntervalSince(start)
            
            ZStack {
                // 1. Fluid Gradient Background
                LinearGradient(
                    colors: [
                        Color(red: 0.1, green: 0.2, blue: 0.4 + sin(elapsed * 0.1) * 0.1),
                        Color(red: 0.3 + cos(elapsed * 0.12) * 0.1, green: 0.1, blue: 0.3),
                        Color(red: 0.1, green: 0.4 + sin(elapsed * 0.08) * 0.1, blue: 0.3)
                    ],
                    startPoint: UnitPoint(x: 0, y: sin(elapsed * 0.2) * 0.5),
                    endPoint: UnitPoint(x: 1, y: 1 - cos(elapsed * 0.15) * 0.5)
                )
                
                // 2. The Memory Layer (User Photo) - faded
                if let image = bgManager.selectedImage {
                    Image(uiImage: image)
                        .resizable()
                        .aspectRatio(contentMode: .fill)
                        .frame(maxWidth: .infinity, maxHeight: .infinity)
                        .blur(radius: 60)
                        .opacity(0.15)
                        .blendMode(.overlay)
                        .ignoresSafeArea()
                }
                
                // 3. Floating Bubbles
                GeometryReader { proxy in
                    let w = proxy.size.width
                    let h = proxy.size.height
                    
                    ForEach(0..<bubbleCount, id: \.self) { i in
                        let size = bubbleSizes[i]
                        let speed = bubbleSpeeds[i]
                        // Rise upwards, reset to bottom when reaching top
                        let verticalProgress = (elapsed * speed + Double(bubblePositions[i].y)).truncatingRemainder(dividingBy: 1.2)
                        let yPos = h + (size * 2) - (CGFloat(verticalProgress) * (h + size * 4))
                        
                        // Gentle horizontal sway
                        let xOff = sin(elapsed * speed * 2 + Double(i)) * 40
                        let xPos = bubblePositions[i].x * w + xOff
                        
                        Circle()
                            .fill(LinearGradient(
                                colors: [.white.opacity(0.3), .white.opacity(0.05), .clear],
                                startPoint: .topLeading,
                                endPoint: .bottomTrailing
                            ))
                            .frame(width: size, height: size)
                            .position(x: xPos, y: yPos + (scrollOffset * 0.1))
                            .overlay(
                                Circle()
                                    .stroke(LinearGradient(
                                        colors: [.white.opacity(0.5), .clear],
                                        startPoint: .topLeading,
                                        endPoint: .bottomTrailing
                                    ), lineWidth: 1)
                            )
                            .blur(radius: size > 60 ? 4 : 1)
                    }
                }
                
                // 4. Cinematic Vignette
                RadialGradient(colors: [.clear, .black.opacity(0.4)], center: .center, startRadius: 100, endRadius: 900)
                    .blendMode(.multiply)
            }
            .ignoresSafeArea()
        }
    }
}
