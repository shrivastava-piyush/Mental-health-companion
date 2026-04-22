package com.wellness.companion.domain.llm

import com.wellness.companion.domain.narrative.MirrorGenerator

class ReflectionEngine(private val engine: LlmEngine) {

    val isReady: Boolean get() = engine.isReady

    suspend fun reflect(title: String, body: String): List<String>? {
        if (!isReady) return null
        val prompt = "Title: $title\n\nContent: $body"
        val response = engine.generate(LlmPrompts.SOCRATIC_REFLECTION, prompt, 150, 0.7f)
        return response.split("\n")
            .map { it.trim().trimStart('1', '2', '3', '.', '-', ' ') }
            .filter { it.isNotBlank() }
    }

    suspend fun reframe(title: String, body: String): String? {
        if (!isReady) return null
        val prompt = "Title: $title\n\nContent: $body"
        return engine.generate(LlmPrompts.REFRAME_LENS, prompt, 100, 0.8f).trim()
    }

    suspend fun narrateMirror(mirror: MirrorGenerator.Mirror): String? {
        if (!isReady) return null
        val data = buildString {
            appendLine("Period: ${mirror.periodLabel}")
            appendLine("Total Moods: ${mirror.totalMoods}")
            appendLine("Total Entries: ${mirror.totalEntries}")
            appendLine("Top Themes: ${mirror.topWords.joinToString { it.first }}")
            if (mirror.highlightSnippet.isNotBlank()) {
                appendLine("Key Reflection: ${mirror.highlightSnippet}")
            }
        }
        return engine.generate(LlmPrompts.PATTERN_NARRATOR, data, 200, 0.7f).trim()
    }

    suspend fun contextualStarter(moodLabel: String?, timeOfDay: String): String? {
        if (!isReady) return null
        val prompt = "Mood: ${moodLabel ?: "Balanced"}, Time: $timeOfDay"
        return engine.generate(LlmPrompts.CONTEXTUAL_STARTER, prompt, 50, 0.9f).trim().trim('"')
    }

    suspend fun goDeeper(bodySoFar: String): String? {
        if (!isReady) return null
        return engine.generate(LlmPrompts.GO_DEEPER, bodySoFar, 60, 0.8f).trim().trim('"')
    }

    suspend fun suggestTitle(body: String): String? {
        if (!isReady) return null
        return engine.generate(LlmPrompts.AUTO_TITLE, body, 20, 0.6f).trim().trim('"')
    }

    suspend fun guidedQuestion(exchanges: List<Pair<String, String>>): String? {
        if (!isReady) return null
        val history = exchanges.joinToString("\n") { "Q: ${it.first}\nA: ${it.second}" }
        return engine.generate(LlmPrompts.GUIDED_QUESTION, history, 60, 0.8f).trim().trim('"')
    }

    suspend fun compileGuided(exchanges: List<Pair<String, String>>): String? {
        if (!isReady) return null
        val interview = exchanges.joinToString("\n") { "Q: ${it.first}\nA: ${it.second}" }
        return engine.generate(LlmPrompts.GUIDED_COMPILE, interview, 512, 0.7f).trim()
    }
}
