package com.wellness.companion.domain.llm

import com.wellness.companion.domain.narrative.MirrorGenerator

class ReflectionEngine(private val llm: LlmEngine) {

    data class Reflection(
        val questions: List<String>,
    )

    data class Reframe(
        val text: String,
    )

    data class PatternNarrative(
        val text: String,
    )

    suspend fun reflect(title: String, body: String): Reflection? {
        if (!llm.isReady || body.isBlank()) return null
        val raw = llm.generate(
            systemPrompt = LlmPrompts.SOCRATIC_REFLECTION,
            userPrompt = LlmPrompts.socraticUserPrompt(title, body),
            maxTokens = 200,
            temperature = 0.8f,
        )
        val questions = raw
            .split("\n")
            .map { it.trim() }
            .filter { it.isNotBlank() && it.endsWith("?") }
            .take(3)
        return if (questions.isNotEmpty()) Reflection(questions) else null
    }

    suspend fun reframe(title: String, body: String): Reframe? {
        if (!llm.isReady || body.isBlank()) return null
        val raw = llm.generate(
            systemPrompt = LlmPrompts.REFRAME_LENS,
            userPrompt = LlmPrompts.reframeUserPrompt(title, body),
            maxTokens = 120,
            temperature = 0.7f,
        )
        return if (raw.isNotBlank()) Reframe(raw.trim()) else null
    }

    suspend fun narrateMirror(mirror: MirrorGenerator.Mirror): PatternNarrative? {
        if (!llm.isReady) return null
        val raw = llm.generate(
            systemPrompt = LlmPrompts.PATTERN_NARRATOR,
            userPrompt = LlmPrompts.patternNarratorUserPrompt(
                avgValence = mirror.avgValence,
                topWords = mirror.topWords,
                totalEntries = mirror.totalEntries,
                totalMoods = mirror.totalMoods,
                callback = mirror.callback,
                highlightSnippet = mirror.highlightSnippet,
            ),
            maxTokens = 140,
            temperature = 0.75f,
        )
        return if (raw.isNotBlank()) PatternNarrative(raw.trim()) else null
    }
}
