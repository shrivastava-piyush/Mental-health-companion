import AVFoundation
import Combine

/// Manages a high-fidelity ambient soundscape.
/// Generates deep, filtered Pink Noise to simulate natural "Sanctuary" sounds like wind or distant waves.
final class AtmosphereManager: ObservableObject {
    private var engine = AVAudioEngine()
    
    @Published var isPlaying = false
    @Published var volume: Float = 0.5 {
        didSet { engine.mainMixerNode.outputVolume = volume }
    }
    
    init() {
        prepareSession()
    }
    
    private func prepareSession() {
        do {
            let session = AVAudioSession.sharedInstance()
            try session.setCategory(.playback, mode: .default, options: [.mixWithOthers])
            try session.setActive(true)
        } catch {
            print("AVAudioSession failed: \(error)")
        }
    }
    
    func start() {
        guard !engine.isRunning else { return }
        
        let format = engine.outputNode.inputFormat(forBus: 0)
        
        // State for the noise generator and filter
        var b0: Float = 0, b1: Float = 0, b2: Float = 0, b3: Float = 0, b4: Float = 0, b5: Float = 0, b6: Float = 0
        var lowPassState: Float = 0
        
        let sourceNode = AVAudioSourceNode { _, _, frameCount, audioBufferList -> OSStatus in
            let ablPointer = UnsafeMutableAudioBufferListPointer(audioBufferList)
            
            for frame in 0..<Int(frameCount) {
                // 1. Generate White Noise
                let white = Float.random(in: -1...1)
                
                // 2. Filter to Pink Noise (Voss-McCartney Algorithm approx)
                // This gives it that organic "rain/wind" quality
                b0 = 0.99886 * b0 + white * 0.0555179
                b1 = 0.99332 * b1 + white * 0.0750759
                b2 = 0.96900 * b2 + white * 0.1538520
                b3 = 0.86650 * b3 + white * 0.3104856
                b4 = 0.55000 * b4 + white * 0.5329522
                b5 = -0.7616 * b5 - white * 0.0168980
                let pink = b0 + b1 + b2 + b3 + b4 + b5 + b6 + white * 0.5362
                b6 = white * 0.115926
                
                // 3. Deep Low-Pass Filter (Simple RC filter)
                // This removes the "hiss" and leaves only the "rumble/ocean"
                let alpha: Float = 0.02 // Very low cutoff
                lowPassState = lowPassState + alpha * (pink - lowPassState)
                
                // 4. Final Output with very soft gain
                let value = lowPassState * 0.03
                
                for buffer in ablPointer {
                    let buf: UnsafeMutablePointer<Float> = buffer.mData!.assumingMemoryBound(to: Float.self)
                    buf[frame] = value
                }
            }
            return noErr
        }
        
        engine.attach(sourceNode)
        engine.connect(sourceNode, to: engine.mainMixerNode, format: format)
        
        do {
            engine.prepare()
            try engine.start()
            engine.mainMixerNode.outputVolume = volume
            isPlaying = true
            print("Sanctuary Atmosphere Started (Organic Noise)")
        } catch {
            print("Atmosphere Engine failed: \(error)")
        }
    }
    
    func stop() {
        engine.stop()
        isPlaying = false
    }
}
