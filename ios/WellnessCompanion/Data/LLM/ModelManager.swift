import Foundation
import Combine

final class ModelManager: ObservableObject {
    enum Status: Equatable {
        case notDownloaded
        case downloading(Float)
        case ready
        case error(String)
    }

    @Published var status: Status = .notDownloaded

    private let modelDir: URL
    private let modelFile: URL
    private static let filename = "reflection-model.gguf"
    private static let minValidSize: Int64 = 50_000_000

    var isDownloaded: Bool { status == .ready }
    var modelPath: String { modelFile.path }

    init() {
        let docs = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
        modelDir = docs.appendingPathComponent("llm")
        modelFile = modelDir.appendingPathComponent(Self.filename)

        if FileManager.default.fileExists(atPath: modelFile.path),
           (try? FileManager.default.attributesOfItem(atPath: modelFile.path)[.size] as? Int64) ?? 0 > Self.minValidSize {
            status = .ready
        }
    }

    func download(url: String) {
        if case .downloading = status { return }
        
        status = .downloading(0.001) 

        Task.detached(priority: .userInitiated) { [self] in
            do {
                try FileManager.default.createDirectory(at: modelDir, withIntermediateDirectories: true)
                let tempFile = modelDir.appendingPathComponent("\(Self.filename).part")
                guard let remoteURL = URL(string: url) else {
                    await MainActor.run { self.status = .error("Invalid URL") }
                    return
                }

                let (asyncBytes, response) = try await URLSession.shared.bytes(for: remoteURL)
                guard let httpResponse = response as? HTTPURLResponse,
                      (200...299).contains(httpResponse.statusCode) else {
                    await MainActor.run { self.status = .error("Server error: \((response as? HTTPURLResponse)?.statusCode ?? 0)") }
                    return
                }

                let totalSize = httpResponse.expectedContentLength
                try? FileManager.default.removeItem(at: tempFile)
                FileManager.default.createFile(atPath: tempFile.path, contents: nil)
                
                let handle = try FileHandle(forWritingTo: tempFile)
                var downloaded: Int64 = 0
                var lastUpdate = Date()
                
                // --- HIGH PERFORMANCE BUFFERING ---
                var buffer = Data()
                buffer.reserveCapacity(64 * 1024) // 64KB Buffer

                for try await byte in asyncBytes {
                    buffer.append(byte)
                    downloaded += 1
                    
                    // Write to disk every 64KB or when finished
                    if buffer.count >= 65536 {
                        try handle.write(contentsOf: buffer)
                        buffer.removeAll(keepingCapacity: true)
                        
                        // Update UI at 30fps max to prevent stutter
                        if Date().timeIntervalSince(lastUpdate) > 0.033 {
                            let progress = totalSize > 0 ? Float(downloaded) / Float(totalSize) : 0
                            await MainActor.run { self.status = .downloading(progress) }
                            lastUpdate = Date()
                        }
                    }
                }
                
                // Final flush
                if !buffer.isEmpty {
                    try handle.write(contentsOf: buffer)
                }
                
                try handle.close()

                try? FileManager.default.removeItem(at: modelFile)
                try FileManager.default.moveItem(at: tempFile, to: modelFile)
                await MainActor.run { self.status = .ready }
            } catch {
                await MainActor.run { self.status = .error(error.localizedDescription) }
            }
        }
    }

    func deleteModel() {
        try? FileManager.default.removeItem(at: modelFile)
        try? FileManager.default.removeItem(at: modelDir.appendingPathComponent("\(Self.filename).part"))
        status = .notDownloaded
    }
}
