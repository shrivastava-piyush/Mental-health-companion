package com.wellness.companion.domain.llm

import com.wellness.companion.domain.narrative.MirrorGenerator

class ReflectionEngine(private val llm: LlmEngine) {

    data class Reflection(val questions: List<String>)
    data class Reframe(val text: String)
    data class PatternNarrative(val text: String)

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

    suspend fun contextualStarter(moodLabel: String?, timeOfDay: String): String? {
        if (!llm.isReady) return null
        val raw = llm.generate(
            systemPrompt = LlmPrompts.CONTEXTUAL_STARTER,
            userPrompt = LlmPrompts.contextualStarterUserPrompt(moodLabel, timeOfDay),
            maxTokens = 40,
            temperature = 0.9f,
        )
        return raw.trim().takeIf { it.isNotBlank() }
    }

    suspend fun goDeeper(bodySoFar: String): String? {
        if (!llm.isReady || bodySoFar.isBlank()) return null
        val raw = llm.generate(
            systemPrompt = LlmPrompts.GO_DEEPER,
            userPrompt = LlmPrompts.goDeeperUserPrompt(bodySoFar),
            maxTokens = 50,
            temperature = 0.8f,
        )
        val cleaned = raw.trim()
        return if (cleaned.isNotBlank() && cleaned.endsWith("?")) cleaned else null
    }

    suspend fun suggestTitle(body: String): String? {
        if (!llm.isReady || body.isBlank()) return null
        val raw = llm.generate(
            systemPrompt = LlmPrompts.AUTO_TITLE,
            userPrompt = LlmPrompts.autoTitleUserPrompt(body),
            maxTokens = 20,
            temperature = 0.7f,
        )
        return raw.trim().take(80).takeIf { it.isNotBlank() }
    }

    suspend fun guidedQuestion(exchanges: List<Pair<String, String>>): String? {
        if (!llm.isReady) return null
        val raw = llm.generate(
            systemPrompt = LlmPrompts.GUIDED_QUESTION,
            userPrompt = LlmPrompts.guidedQuestionUserPrompt(exchanges, exchanges.isEmpty()),
            maxTokens = 50,
            temperature = 0.85f,
        )
        return raw.trim().takeIf { it.isNotBlank() }
    }

    suspend fun compileGuided(exchanges: List<Pair<String, String>>): String? {
        if (!llm.isReady || exchanges.isEmpty()) return null
        val raw = llm.generate(
            systemPrompt = LlmPrompts.GUIDED_COMPILE,
            userPrompt = LlmPrompts.guidedCompileUserPrompt(exchanges),
            maxTokens = 400,
            temperature = 0.6f,
        )
        return raw.trim().takeIf { it.isNotBlank() }
    }
}
