#include <jni.h>
#include <string>
#include <android/log.h>
#include <vector>
#include "llama.h"

#define TAG "LlmBridge"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

static std::string jstring_to_string(JNIEnv *env, jstring jstr) {
    const char *raw = env->GetStringUTFChars(jstr, nullptr);
    std::string result(raw);
    env->ReleaseStringUTFChars(jstr, raw);
    return result;
}

struct LlmHandle {
    llama_model *model;
    llama_context *ctx;
    const llama_vocab *vocab;
};

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

    llama_model *model = llama_model_load_from_file(model_path.c_str(), params);
    if (!model) {
        LOGE("Failed to load model");
        return 0;
    }

    auto ctx_params = llama_context_default_params();
    ctx_params.n_ctx = context_size;
    ctx_params.n_threads = 4;

    llama_context *ctx = llama_init_from_model(model, ctx_params);
    if (!ctx) {
        LOGE("Failed to create context");
        llama_model_free(model);
        return 0;
    }

    auto *handle = new LlmHandle{model, ctx, llama_model_get_vocab(model)};
    LOGI("Model loaded successfully");
    return reinterpret_cast<jlong>(handle);
}

JNIEXPORT void JNICALL
Java_com_wellness_companion_data_llm_LlamaBridge_unloadModel(
    JNIEnv * /* env */, jobject /* this */, jlong handle_ptr
) {
    if (handle_ptr == 0) return;
    auto *handle = reinterpret_cast<LlmHandle *>(handle_ptr);
    llama_free(handle->ctx);
    llama_model_free(handle->model);
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
    if (handle_ptr == 0) return env->NewStringUTF("");
    auto *handle = reinterpret_cast<LlmHandle *>(handle_ptr);
    auto *model = handle->model;
    auto *ctx = handle->ctx;
    auto *vocab = handle->vocab;

    std::string sys = jstring_to_string(env, system_prompt);
    std::string usr = jstring_to_string(env, user_prompt);

    std::string prompt = "<|im_start|>system\n" + sys + "<|im_end|>\n<|im_start|>user\n" + usr + "<|im_end|>\n<|im_start|>assistant\n";

    std::vector<llama_token> tokens(prompt.size() + 32);
    int n_tokens = llama_tokenize(vocab, prompt.c_str(), prompt.size(), tokens.data(), tokens.size(), true, true);
    if (n_tokens < 0) return env->NewStringUTF("");
    tokens.resize(n_tokens);

    llama_memory_clear(llama_get_memory(ctx), true);

    llama_batch batch = llama_batch_init(n_tokens, 0, 1);
    for (int i = 0; i < n_tokens; i++) {
        batch.token[i] = tokens[i];
        batch.pos[i] = i;
        batch.n_seq_id[i] = 1;
        batch.seq_id[i][0] = 0;
        batch.logits[i] = (i == n_tokens - 1);
    }
    batch.n_tokens = n_tokens;

    if (llama_decode(ctx, batch) != 0) {
        llama_batch_free(batch);
        return env->NewStringUTF("");
    }

    std::string result;
    auto *smpl = llama_sampler_chain_init(llama_sampler_chain_default_params());
    llama_sampler_chain_add(smpl, llama_sampler_init_temp(temperature));
    llama_sampler_chain_add(smpl, llama_sampler_init_top_p(top_p, 1));
    llama_sampler_chain_add(smpl, llama_sampler_init_dist(1234));

    for (int i = 0; i < max_tokens; i++) {
        llama_token next = llama_sampler_sample(smpl, ctx, -1);
        if (llama_vocab_is_eog(vocab, next)) break;

        char buf[256];
        int len = llama_token_to_piece(vocab, next, buf, sizeof(buf), 0, false);
        if (len > 0) result.append(buf, len);
        if (result.find("<|im_end|>") != std::string::npos) break;

        llama_batch_free(batch);
        batch = llama_batch_init(1, 0, 1);
        batch.token[0] = next;
        batch.pos[0] = n_tokens + i;
        batch.n_seq_id[0] = 1;
        batch.seq_id[0][0] = 0;
        batch.logits[0] = true;
        batch.n_tokens = 1;

        if (llama_decode(ctx, batch) != 0) break;
    }

    llama_sampler_free(smpl);
    llama_batch_free(batch);

    size_t pos = result.find("<|im_end|>");
    if (pos != std::string::npos) result = result.substr(0, pos);
    
    return env->NewStringUTF(result.c_str());
}

} // extern "C"
