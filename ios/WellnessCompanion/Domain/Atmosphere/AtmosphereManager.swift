import AVFoundation
import Combine

/// Manages the ambient soundscape of the application.
/// Uses AVAudioEngine to generate a procedurally shifting ambient drone.
final class AtmosphereManager: ObservableObject {
    private var engine = AVAudioEngine()
    
    @Published var isPlaying = false
    @Published var volume: Float = 0.8 { // Increased default volume
        didSet { engine.mainMixerNode.outputVolume = volume }
    }
    
    init() {
        prepareSession()
    }
    
    private func prepareSession() {
        do {
            let session = AVAudioSession.sharedInstance()
            // We use .playback to ensure it plays even if the silent switch is on (like Timely/Calm)
            try session.setCategory(.playback, mode: .default, options: [.mixWithOthers])
            try session.setActive(true)
            print("AVAudioSession Active")
        } catch {
            print("AVAudioSession failed: \(error)")
        }
    }
    
    func start() {
        guard !engine.isRunning else { return }
        
        let format = engine.outputNode.inputFormat(forBus: 0)
        let sampleRate = Float(format.sampleRate)
        
        // Harmonic frequencies for a deep, serene drone (F-major/D-minor feel)
        let frequencies: [Float] = [87.31, 130.81, 174.61, 261.63] // F2, C3, F3, C4
        
        for (index, freq) in frequencies.enumerated() {
            var phase: Float = 0
            let phaseStep = (2.0 * .pi * freq) / sampleRate
            
            let sourceNode = AVAudioSourceNode { _, _, frameCount, audioBufferList -> OSStatus in
                let ablPointer = UnsafeMutableAudioBufferListPointer(audioBufferList)
                for frame in 0..<Int(frameCount) {
                    // Very slow LFO for volume breathing
                    let lfo = sin(Float(index) + phase * 0.00003) * 0.3 + 0.7
                    // Harmonic wave
                    let value = sin(phase) * (0.08 / Float(frequencies.count)) * lfo
                    
                    for buffer in ablPointer {
                        let buf: UnsafeMutablePointer<Float> = buffer.mData!.assumingMemoryBound(to: Float.self)
                        buf[frame] = value
                    }
                    phase += phaseStep
                }
                return noErr
            }
            
            engine.attach(sourceNode)
            engine.connect(sourceNode, to: engine.mainMixerNode, format: format)
        }
        
        do {
            engine.prepare()
            try engine.start()
            engine.mainMixerNode.outputVolume = volume
            isPlaying = true
            print("Atmosphere Engine Playing at Volume: \(volume)")
        } catch {
            print("Atmosphere Engine failed: \(error)")
        }
    }
    
    func stop() {
        engine.stop()
        isPlaying = false
    }
}
