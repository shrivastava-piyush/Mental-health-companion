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
        status = .downloading(0)

        Task.detached(priority: .utility) { [self] in
            do {
                try FileManager.default.createDirectory(at: modelDir, withIntermediateDirectories: true)
                let tempFile = modelDir.appendingPathComponent("\(Self.filename).part")
                guard let remoteURL = URL(string: url) else {
                    await MainActor.run { self.status = .error("Invalid URL") }
                    return
                }

                var request = URLRequest(url: remoteURL)
                request.timeoutInterval = 30

                if FileManager.default.fileExists(atPath: tempFile.path),
                   let attrs = try? FileManager.default.attributesOfItem(atPath: tempFile.path),
                   let size = attrs[.size] as? Int64, size > 0 {
                    request.setValue("bytes=\(size)-", forHTTPHeaderField: "Range")
                }

                let (asyncBytes, response) = try await URLSession.shared.bytes(for: request)
                guard let httpResponse = response as? HTTPURLResponse,
                      (200...206).contains(httpResponse.statusCode) else {
                    await MainActor.run { self.status = .error("Download failed") }
                    return
                }

                let totalSize = httpResponse.expectedContentLength
                let append = httpResponse.statusCode == 206
                if !append {
                    try? FileManager.default.removeItem(at: tempFile)
                }

                let handle = try FileHandle(forWritingTo: {
                    if !FileManager.default.fileExists(atPath: tempFile.path) {
                        FileManager.default.createFile(atPath: tempFile.path, contents: nil)
                    }
                    return tempFile
                }())
                if append { handle.seekToEndOfFile() }

                var downloaded: Int64 = append ? (try? FileManager.default.attributesOfItem(atPath: tempFile.path)[.size] as? Int64) ?? 0 : 0
                var buffer = Data()
                buffer.reserveCapacity(8192)

                for try await byte in asyncBytes {
                    buffer.append(byte)
                    if buffer.count >= 8192 {
                        handle.write(buffer)
                        downloaded += Int64(buffer.count)
                        buffer.removeAll(keepingCapacity: true)
                        let progress = totalSize > 0 ? Float(downloaded) / Float(totalSize) : 0
                        await MainActor.run { self.status = .downloading(progress) }
                    }
                }
                if !buffer.isEmpty {
                    handle.write(buffer)
                }
                handle.closeFile()

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
