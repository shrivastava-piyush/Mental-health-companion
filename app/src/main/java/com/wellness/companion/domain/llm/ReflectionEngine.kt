package com.wellness.companion.domain.llm

import com.wellness.companion.domain.narrative.MirrorGenerator
import com.wellness.companion.data.db.entities.JournalEntry

class ReflectionEngine(private val engine: LlmEngine) {

    val isReady: Boolean get() = engine.isReady

    suspend fun reflect(title: String, body: String): List<String>? {
        if (!isReady) return null
        val prompt = "Title: $title\n\nContent: $body"
        val response = engine.generate(LlmPrompts.SOCRATIC_REFLECTION, prompt, 150, 0.9f)
        return response.split("\n")
            .map { it.trim().trimStart('1', '2', '3', '.', '-', ' ') }
            .filter { it.isNotBlank() && it.endsWith("?") }
    }

    suspend fun reframe(title: String, body: String): String? {
        if (!isReady) return null
        val prompt = "Title: $title\n\nContent: $body"
        return engine.generate(LlmPrompts.REFRAME_LENS, prompt, 100, 0.7f).trim()
    }

    suspend fun narrateMirror(mirror: MirrorGenerator.Mirror): String? {
        if (!isReady) return null
        val data = "Valence: ${mirror.avgValence}, Keywords: ${mirror.topWords.joinToString { it.first }}"
        return engine.generate(LlmPrompts.PATTERN_NARRATOR, data, 200, 0.5f).trim()
    }

    suspend fun contextualStarter(moodLabel: String?, timeOfDay: String): String? {
        if (!isReady) return null
        val prompt = "Mood: ${moodLabel ?: "Balanced"}, Time: $timeOfDay"
        return engine.generate(LlmPrompts.CONTEXTUAL_STARTER, prompt, 50, 1.0f).trim().trim('"')
    }

    suspend fun suggestTitle(body: String): String? {
        if (!isReady) return null
        return engine.generate(LlmPrompts.AUTO_TITLE, body, 20, 0.6f).trim().trim('"').trim('.')
    }

    suspend fun guidedQuestion(exchanges: List<Pair<String, String>>, context: String = ""): String? {
        if (!isReady) return null
        val user = buildString {
            if (context.isNotEmpty()) appendLine("Context: $context\n")
            if (exchanges.isEmpty()) {
                appendLine("Start the analytic session.")
            } else {
                appendLine("Recent exchanges:")
                exchanges.takeLast(3).forEach { (q, a) -> appendLine("Q: $q\nA: $a") }
                appendLine("\nAsk the next sharp question.")
            }
        }
        return engine.generate(LlmPrompts.GUIDED_QUESTION, user, 60, 0.8f).trim().trim('"')
    }

    suspend fun compileGuided(exchanges: List<Pair<String, String>>): String? {
        if (!isReady) return null
        val interview = exchanges.joinToString("\n") { "Q: ${it.first}\nA: ${it.second}" }
        return engine.generate(LlmPrompts.GUIDED_COMPILE, interview, 512, 0.7f).trim()
    }

    suspend fun synthesizeInsight(entries: List<JournalEntry>): String? {
        if (!isReady || entries.size < 2) return null
        val combined = entries.take(3).joinToString("\n---\n") { "${it.title}: ${it.body}" }
        val system = "Persona: The Analytic Observer. Task: Find the 'hidden thread' connecting these 3 notes. What is the user not saying directly? Constraint: Under 40 words. Be sharp."
        return engine.generate(system, combined, 100, 0.75f).trim()
    }
}
