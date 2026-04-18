import Foundation

final class LlamaEngine {
    let modelPath: String
    private var model: OpaquePointer?
    private var ctx: OpaquePointer?
    private let lock = NSLock()

    var isReady: Bool { ctx != nil }

    init(modelPath: String) {
        self.modelPath = modelPath
    }

    func load() {
        lock.lock()
        defer { lock.unlock() }
        guard ctx == nil else { return }

        llama_backend_init()
        var mparams = llama_model_default_params()
        mparams.n_gpu_layers = 0

        model = llama_load_model_from_file(modelPath, mparams)
        guard let model else { return }

        var cparams = llama_context_default_params()
        cparams.n_ctx = 2048
        cparams.n_threads = 4

        ctx = llama_new_context_with_model(model, cparams)
    }

    func unload() {
        lock.lock()
        defer { lock.unlock() }
        if let ctx { llama_free(ctx) }
        if let model { llama_free_model(model) }
        self.ctx = nil
        self.model = nil
        llama_backend_free()
    }

    func generate(system: String, user: String, maxTokens: Int, temperature: Float) async -> String {
        await withCheckedContinuation { continuation in
            DispatchQueue.global(qos: .userInitiated).async { [self] in
                lock.lock()
                defer { lock.unlock() }
                guard let model, let ctx else {
                    continuation.resume(returning: "")
                    return
                }

                let prompt = "<|im_start|>system\n\(system)<|im_end|>\n<|im_start|>user\n\(user)<|im_end|>\n<|im_start|>assistant\n"

                let maxTokenCount = Int32(prompt.utf8.count + 512)
                var tokens = [llama_token](repeating: 0, count: Int(maxTokenCount))
                let nTokens = llama_tokenize(model, prompt, Int32(prompt.utf8.count), &tokens, maxTokenCount, true, false)
                guard nTokens > 0 else {
                    continuation.resume(returning: "")
                    return
                }
                tokens = Array(tokens.prefix(Int(nTokens)))

                llama_kv_cache_clear(ctx)

                var batch = llama_batch_init(nTokens, 0, 1)
                for (i, token) in tokens.enumerated() {
                    llama_batch_add(&batch, token, Int32(i), [0], false)
                }
                batch.logits[Int(batch.n_tokens - 1)] = 1

                guard llama_decode(ctx, batch) == 0 else {
                    llama_batch_free(batch)
                    continuation.resume(returning: "")
                    return
                }

                var result = ""
                let nVocab = llama_n_vocab(model)

                for n in 0..<maxTokens {
                    guard let logits = llama_get_logits_ith(ctx, batch.n_tokens - 1) else { break }

                    var candidates = (0..<Int(nVocab)).map { llama_token_data(id: Int32($0), logit: logits[$0], p: 0) }
                    var candidatesArr = llama_token_data_array(data: &candidates, size: Int(nVocab), sorted: false)

                    llama_sample_temp(ctx, &candidatesArr, temperature)
                    llama_sample_top_p(ctx, &candidatesArr, 0.9, 1)
                    let newToken = llama_sample_token(ctx, &candidatesArr)

                    if llama_token_is_eog(model, newToken) { break }

                    var buf = [CChar](repeating: 0, count: 256)
                    let len = llama_token_to_piece(model, newToken, &buf, 256, 0, false)
                    if len > 0 {
                        let piece = String(cString: buf)
                        result += piece
                    }

                    if result.contains("<|im_end|>") {
                        result = String(result[..<result.range(of: "<|im_end|>")!.lowerBound])
                        break
                    }

                    llama_batch_free(batch)
                    batch = llama_batch_init(1, 0, 1)
                    llama_batch_add(&batch, newToken, Int32(Int(nTokens) + n), [0], true)
                    if llama_decode(ctx, batch) != 0 { break }
                }

                llama_batch_free(batch)
                continuation.resume(returning: result.trimmingCharacters(in: .whitespacesAndNewlines))
            }
        }
    }
}
