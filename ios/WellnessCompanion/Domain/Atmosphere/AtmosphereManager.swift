import AVFoundation
import Combine

/// Manages a high-fidelity adaptive ambient soundscape.
/// Generates soft, filtered textures that adapt to the user's last mood.
final class AtmosphereManager: ObservableObject {
    private var engine = AVAudioEngine()
    
    @Published var isPlaying = false
    @Published var volume: Float = 0.5 {
        didSet { engine.mainMixerNode.outputVolume = volume }
    }
    
    // Parameters for mood adaptation
    private var baseFrequencies: [Float] = [110.0, 164.81, 220.0, 329.63]
    private var noiseAlpha: Float = 0.015
    
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
    
    /// Updates the soundscape based on the last mood valence.
    /// Positive: Lighter, higher frequency harmonics.
    /// Negative: Deeper, warmer, more grounding noise floor.
    func adaptTo(valence: Int) {
        if valence > 20 {
            // Uplifting / Light (Feminine crystalline touch)
            baseFrequencies = [220.0, 329.63, 440.0, 659.25]
            noiseAlpha = 0.01
        } else if valence < -20 {
            // Grounding / Deep
            baseFrequencies = [87.31, 130.81, 174.61, 261.63]
            noiseAlpha = 0.025
        } else {
            // Balanced
            baseFrequencies = [110.0, 164.81, 220.0, 329.63]
            noiseAlpha = 0.015
        }
        
        // Restart engine if running to apply new nodes (or we could use parameters if we had custom nodes)
        if engine.isRunning {
            stop()
            start()
        }
    }
    
    func start() {
        guard !engine.isRunning else { return }
        
        engine = AVAudioEngine() // Fresh engine for parameter changes
        let format = engine.outputNode.inputFormat(forBus: 0)
        let sampleRate = Float(format.sampleRate)
        
        // 1. Organic Noise Layer (The "Sanctuary" floor)
        var lowPassState: Float = 0
        let noiseNode = AVAudioSourceNode { [weak self] _, _, frameCount, audioBufferList -> OSStatus in
            guard let self = self else { return noErr }
            let ablPointer = UnsafeMutableAudioBufferListPointer(audioBufferList)
            for frame in 0..<Int(frameCount) {
                let white = Float.random(in: -1...1)
                lowPassState = lowPassState + self.noiseAlpha * (white - lowPassState)
                let value = lowPassState * 0.02
                for buffer in ablPointer {
                    let buf: UnsafeMutablePointer<Float> = buffer.mData!.assumingMemoryBound(to: Float.self)
                    buf[frame] = value
                }
            }
            return noErr
        }
        
        engine.attach(noiseNode)
        engine.connect(noiseNode, to: engine.mainMixerNode, format: format)
        
        // 2. Resonant Harmonics (The "Feminine" touch)
        // Soft, pulsating sine waves with heavy filtering
        for (index, freq) in baseFrequencies.enumerated() {
            var phase: Float = 0
            let phaseStep = (2.0 * .pi * freq) / sampleRate
            
            let harmonicNode = AVAudioSourceNode { _, _, frameCount, audioBufferList -> OSStatus in
                let ablPointer = UnsafeMutableAudioBufferListPointer(audioBufferList)
                for frame in 0..<Int(frameCount) {
                    // Very slow volume modulation
                    let lfo = sin(Float(index) + phase * 0.00001) * 0.4 + 0.6
                    let value = sin(phase) * (0.015 / Float(self.baseFrequencies.count)) * lfo
                    for buffer in ablPointer {
                        let buf: UnsafeMutablePointer<Float> = buffer.mData!.assumingMemoryBound(to: Float.self)
                        buf[frame] = value
                    }
                    phase += phaseStep
                }
                return noErr
            }
            
            engine.attach(harmonicNode)
            engine.connect(harmonicNode, to: engine.mainMixerNode, format: format)
        }
        
        do {
            engine.prepare()
            try engine.start()
            engine.mainMixerNode.outputVolume = volume
            isPlaying = true
        } catch {
            print("Atmosphere Engine failed: \(error)")
        }
    }
    
    func stop() {
        engine.stop()
        isPlaying = false
    }
}
