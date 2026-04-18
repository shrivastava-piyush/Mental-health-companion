package com.wellness.companion.data.llm

import com.wellness.companion.domain.llm.LlmEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class LlamaEngine(private val modelPath: String) : LlmEngine {

    private val mutex = Mutex()
    private var handle: Long = 0L

    override val isReady: Boolean get() = handle != 0L

    suspend fun load() = withContext(Dispatchers.IO) {
        mutex.withLock {
            if (handle != 0L) return@withContext
            handle = LlamaBridge.loadModel(modelPath, contextSize = 2048)
        }
    }

    fun unload() {
        if (handle != 0L) {
            LlamaBridge.unloadModel(handle)
            handle = 0L
        }
    }

    override suspend fun generate(
        systemPrompt: String,
        userPrompt: String,
        maxTokens: Int,
        temperature: Float,
    ): String = withContext(Dispatchers.IO) {
        mutex.withLock {
            check(handle != 0L) { "Model not loaded" }
            LlamaBridge.generate(
                handle = handle,
                systemPrompt = systemPrompt,
                userPrompt = userPrompt,
                maxTokens = maxTokens,
                temperature = temperature,
                topP = 0.9f,
            )
        }
    }
}
