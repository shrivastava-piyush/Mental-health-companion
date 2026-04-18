package com.wellness.companion.domain.llm

interface LlmEngine {

    val isReady: Boolean

    suspend fun generate(
        systemPrompt: String,
        userPrompt: String,
        maxTokens: Int = 256,
        temperature: Float = 0.7f,
    ): String
}
