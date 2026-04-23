import SwiftUI
import AVKit

/// A high-fidelity animated persona representing the AI intelligence ("The Mirror").
/// Uses a high-performance video loop with a glassy mask.
struct MirrorAvatar: View {
    private let videoURL = URL(string: "https://assets.mixkit.co/videos/preview/mixkit-abstract-flowing-teal-and-blue-smoke-24151-large.mp4")!
    @State private var player: AVLooperPlayer?

    var body: some View {
        ZStack {
            if let player = player {
                VideoPlayerContainer(player: player.queuePlayer)
                    .aspectRatio(contentMode: .fill)
                    .frame(width: 120, height: 120)
                    .clipShape(Circle())
                    .blur(radius: 2)
                    .overlay(
                        Circle()
                            .stroke(LinearGradient(colors: [.cyan.opacity(0.5), .clear], startPoint: .topLeading, endPoint: .bottomTrailing), lineWidth: 2)
                    )
                    .shadow(color: .cyan.opacity(0.3), radius: 20)
            } else {
                Circle()
                    .fill(.white.opacity(0.05))
                    .frame(width: 120, height: 120)
            }
        }
        .onAppear {
            self.player = AVLooperPlayer(url: videoURL)
            self.player?.play()
        }
        .onDisappear {
            self.player?.stop()
        }
    }
}

/// Helper to loop video indefinitely
class AVLooperPlayer {
    let queuePlayer = AVQueuePlayer()
    private var looper: AVPlayerLooper?
    
    init(url: URL) {
        let item = AVPlayerItem(url: url)
        looper = AVPlayerLooper(player: queuePlayer, templateItem: item)
        queuePlayer.isMuted = true
    }
    
    func play() { queuePlayer.play() }
    func stop() { queuePlayer.pause() }
}

struct VideoPlayerContainer: UIViewControllerRepresentable {
    let player: AVQueuePlayer
    func makeUIViewController(context: Context) -> AVPlayerViewController {
        let controller = AVPlayerViewController()
        controller.player = player
        controller.showsPlaybackControls = false
        controller.videoGravity = .resizeAspectFill
        return controller
    }
    func updateUIViewController(_ uiViewController: AVPlayerViewController, context: Context) {}
}
