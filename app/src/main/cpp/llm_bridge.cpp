#include <jni.h>
#include <string>
#include <android/log.h>
#include "llama.h"
#include "common.h"

#define TAG "LlmBridge"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

static std::string jstring_to_string(JNIEnv *env, jstring jstr) {
    const char *raw = env->GetStringUTFChars(jstr, nullptr);
    std::string result(raw);
    env->ReleaseStringUTFChars(jstr, raw);
    return result;
}

extern "C" {

JNIEXPORT jlong JNICALL
Java_com_wellness_companion_data_llm_LlamaBridge_loadModel(
    JNIEnv *env, jobject /* this */,
    jstring path, jint context_size, jint gpu_layers
) {
    llama_backend_init();

    auto params = llama_model_default_params();
    params.n_gpu_layers = gpu_layers;

    std::string model_path = jstring_to_string(env, path);
    LOGI("Loading model: %s", model_path.c_str());

    llama_model *model = llama_load_model_from_file(model_path.c_str(), params);
    if (!model) {
        LOGE("Failed to load model");
        return 0;
    }

    auto ctx_params = llama_context_default_params();
    ctx_params.n_ctx = context_size;
    ctx_params.n_threads = 4;

    llama_context *ctx = llama_new_context_with_model(model, ctx_params);
    if (!ctx) {
        LOGE("Failed to create context");
        llama_free_model(model);
        return 0;
    }

    // Pack model + context into a simple struct on the heap.
    struct LlmHandle {
        llama_model *model;
        llama_context *ctx;
    };
    auto *handle = new LlmHandle{model, ctx};
    LOGI("Model loaded successfully");
    return reinterpret_cast<jlong>(handle);
}

JNIEXPORT void JNICALL
Java_com_wellness_companion_data_llm_LlamaBridge_unloadModel(
    JNIEnv * /* env */, jobject /* this */, jlong handle_ptr
) {
    if (handle_ptr == 0) return;
    struct LlmHandle {
        llama_model *model;
        llama_context *ctx;
    };
    auto *handle = reinterpret_cast<LlmHandle *>(handle_ptr);
    llama_free(handle->ctx);
    llama_free_model(handle->model);
    delete handle;
    llama_backend_free();
    LOGI("Model unloaded");
}

JNIEXPORT jstring JNICALL
Java_com_wellness_companion_data_llm_LlamaBridge_generate(
    JNIEnv *env, jobject /* this */,
    jlong handle_ptr,
    jstring system_prompt,
    jstring user_prompt,
    jint max_tokens,
    jfloat temperature,
    jfloat top_p
) {
    if (handle_ptr == 0) {
        return env->NewStringUTF("");
    }

    struct LlmHandle {
        llama_model *model;
        llama_context *ctx;
    };
    auto *handle = reinterpret_cast<LlmHandle *>(handle_ptr);

    std::string sys = jstring_to_string(env, system_prompt);
    std::string usr = jstring_to_string(env, user_prompt);

    // Build chat-style prompt using ChatML format (works with most GGUF models).
    std::string prompt =
        "<|im_start|>system\n" + sys + "<|im_end|>\n"
        "<|im_start|>user\n" + usr + "<|im_end|>\n"
        "<|im_start|>assistant\n";

    // Tokenize.
    auto *model = handle->model;
    auto *ctx = handle->ctx;
    int n_ctx = llama_n_ctx(ctx);

    std::vector<llama_token> tokens(n_ctx);
    int n_tokens = llama_tokenize(
        model, prompt.c_str(), prompt.size(),
        tokens.data(), tokens.size(),
        true, false
    );
    if (n_tokens < 0) {
        LOGE("Tokenization failed");
        return env->NewStringUTF("");
    }
    tokens.resize(n_tokens);

    // Clear KV cache for fresh generation.
    llama_kv_cache_clear(ctx);

    // Evaluate prompt tokens.
    llama_batch batch = llama_batch_init(n_tokens, 0, 1);
    for (int i = 0; i < n_tokens; i++) {
        llama_batch_add(batch, tokens[i], i, {0}, false);
    }
    batch.logits[batch.n_tokens - 1] = true;

    if (llama_decode(ctx, batch) != 0) {
        LOGE("Decode failed");
        llama_batch_free(batch);
        return env->NewStringUTF("");
    }

    // Sample tokens.
    std::string result;
    int n_generated = 0;

    while (n_generated < max_tokens) {
        auto *logits = llama_get_logits_ith(ctx, batch.n_tokens - 1);
        int n_vocab = llama_n_vocab(model);

        std::vector<llama_token_data> candidates(n_vocab);
        for (int i = 0; i < n_vocab; i++) {
            candidates[i] = {i, logits[i], 0.0f};
        }
        llama_token_data_array candidates_arr = {candidates.data(), (size_t)n_vocab, false};

        llama_sample_temp(ctx, &candidates_arr, temperature);
        llama_sample_top_p(ctx, &candidates_arr, top_p, 1);
        llama_token new_token = llama_sample_token(ctx, &candidates_arr);

        if (llama_token_is_eog(model, new_token)) break;

        char buf[256];
        int len = llama_token_to_piece(model, new_token, buf, sizeof(buf), 0, false);
        if (len > 0) {
            result.append(buf, len);
        }

        // Check for ChatML end tag.
        if (result.find("<|im_end|>") != std::string::npos) {
            auto pos = result.find("<|im_end|>");
            result = result.substr(0, pos);
            break;
        }

        // Prepare next batch.
        llama_batch_free(batch);
        batch = llama_batch_init(1, 0, 1);
        llama_batch_add(batch, new_token, n_tokens + n_generated, {0}, true);

        if (llama_decode(ctx, batch) != 0) {
            LOGE("Decode step failed");
            break;
        }

        n_generated++;
    }

    llama_batch_free(batch);

    // Trim whitespace.
    while (!result.empty() && (result.back() == ' ' || result.back() == '\n')) {
        result.pop_back();
    }

    return env->NewStringUTF(result.c_str());
}

} // extern "C"
