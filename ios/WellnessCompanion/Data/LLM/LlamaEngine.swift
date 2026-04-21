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

        model = llama_model_load_from_file(modelPath, mparams)
        guard let model else { return }

        var cparams = llama_context_default_params()
        cparams.n_ctx = 2048
        cparams.n_threads = 4
        cparams.n_threads_batch = 4

        ctx = llama_init_from_model(model, cparams)
    }

    func unload() {
        lock.lock()
        defer { lock.unlock() }
        if let ctx { llama_free(ctx) }
        if let model { llama_model_free(model) }
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

                let vocab = llama_model_get_vocab(model)
                let prompt = "<|im_start|>system\n\(system)<|im_end|>\n<|im_start|>user\n\(user)<|im_end|>\n<|im_start|>assistant\n"

                let maxTokenCount = Int32(prompt.utf8.count + 512)
                var tokens = [llama_token](repeating: 0, count: Int(maxTokenCount))
                let nTokens = llama_tokenize(vocab, prompt, Int32(prompt.utf8.count), &tokens, maxTokenCount, true, true)
                guard nTokens > 0 else {
                    continuation.resume(returning: "")
                    return
                }
                tokens = Array(tokens.prefix(Int(nTokens)))

                llama_memory_clear(llama_get_memory(ctx), true)

                var batch = llama_batch_init(nTokens, 0, 1)
                for (i, token) in tokens.enumerated() {
                    llama_batch_add(&batch, token, Int32(i), [0], i == tokens.count - 1)
                }

                guard llama_decode(ctx, batch) == 0 else {
                    llama_batch_free(batch)
                    continuation.resume(returning: "")
                    return
                }

                var result = ""
                
                let sparams = llama_sampler_chain_default_params()
                let smpl = llama_sampler_chain_init(sparams)
                llama_sampler_chain_add(smpl, llama_sampler_init_temp(temperature))
                llama_sampler_chain_add(smpl, llama_sampler_init_top_p(0.9, 1))
                llama_sampler_chain_add(smpl, llama_sampler_init_dist(UInt32(Date().timeIntervalSince1970)))

                for n in 0..<maxTokens {
                    let newToken = llama_sampler_sample(smpl, ctx, -1)

                    if llama_vocab_is_eog(vocab, newToken) { break }

                    var buf = [CChar](repeating: 0, count: 256)
                    let len = llama_token_to_piece(vocab, newToken, &buf, 256, 0, false)
                    if len > 0 {
                        let piece = String(cString: buf)
                        result += piece
                    }

                    if result.contains("<|im_end|>") {
                        let range = result.range(of: "<|im_end|>")!
                        result = String(result[..<range.lowerBound])
                        break
                    }

                    llama_batch_free(batch)
                    batch = llama_batch_init(1, 0, 1)
                    llama_batch_add(&batch, newToken, Int32(Int(nTokens) + n), [0], true)
                    if llama_decode(ctx, batch) != 0 { break }
                }

                llama_sampler_free(smpl)
                llama_batch_free(batch)
                continuation.resume(returning: result.trimmingCharacters(in: .whitespacesAndNewlines))
            }
        }
    }
}

private func llama_batch_add(_ batch: inout llama_batch, _ id: llama_token, _ pos: llama_pos, _ seq_ids: [llama_seq_id], _ logits: Bool) {
    batch.token![Int(batch.n_tokens)] = id
    batch.pos![Int(batch.n_tokens)] = pos
    batch.n_seq_id[Int(batch.n_tokens)] = Int32(seq_ids.count)
    for i in 0..<seq_ids.count {
        batch.seq_id[Int(batch.n_tokens)]![Int(i)] = seq_ids[i]
    }
    batch.logits![Int(batch.n_tokens)] = logits ? 1 : 0

    batch.n_tokens += 1
}
