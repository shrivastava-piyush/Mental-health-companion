package com.wellness.companion.data.llm

object LlamaBridge {

    init {
        System.loadLibrary("llm_bridge")
    }

    external fun loadModel(path: String, contextSize: Int = 2048, gpuLayers: Int = 0): Long
    external fun unloadModel(handle: Long)
    external fun generate(
        handle: Long,
        systemPrompt: String,
        userPrompt: String,
        maxTokens: Int,
        temperature: Float,
        topP: Float,
    ): String
}
