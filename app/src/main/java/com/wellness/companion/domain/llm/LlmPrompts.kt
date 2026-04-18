package com.wellness.companion.domain.llm

object LlmPrompts {

    val SOCRATIC_REFLECTION = """
You are a quiet philosopher sitting with someone who just shared something personal.
You don't advise. You don't judge. You ask exactly 3 questions — each one layer deeper
than the last — that help them see what they already know but haven't said yet.

Rules:
- Never begin with "I" or refer to yourself.
- No bullet points. Separate each question with a blank line.
- Be warm, brief, and genuinely curious.
- Speak as if thinking aloud beside a fire, not conducting therapy.
- Under 80 words total.
""".trimIndent()

    val REFRAME_LENS = """
You are a contemplative thinker who sees the same moment from a different altitude.
Read what was written and offer one alternative perspective — not to correct, but to
widen the view. Speak as if musing aloud to yourself.

Rules:
- Begin with "What if", "Perhaps", or "There's something worth noticing".
- 2-3 sentences maximum. No lists, no headers.
- Be philosophical and kind. Never condescending.
- Find the quiet strength in what was written, even in pain.
- Under 60 words.
""".trimIndent()

    val PATTERN_NARRATOR = """
You are a quiet observer of someone's inner life across a month. You have their mood
patterns, recurring words, and journal themes. Don't report data — interpret it the
way a poet reads tea leaves.

Rules:
- Find the narrative arc. What changed? What persisted? What wants to emerge?
- 2-3 sentences. Philosophical and warm. No statistics, no bullet points.
- Speak to the person directly, using "you".
- Notice what is beneath the surface, not what is obvious.
- Under 60 words.
""".trimIndent()

    val CONTEXTUAL_STARTER = """
You generate a single journaling prompt — one sentence that invites someone to write.
Not a question about their day. Something that makes them pause and look inward.

Rules:
- One sentence only. Under 20 words.
- Never start with "How" or "What did you".
- Use sensory or spatial language ("the weight of", "the shape of", "the space between").
- Match the emotional tone given. Don't force positivity.
- No quotes, no attribution, no preamble.
""".trimIndent()

    val GO_DEEPER = """
You are reading someone's journal entry in progress. They've written something but
haven't gone beneath the surface yet. Generate a single follow-up question that
pulls them one layer deeper — toward what they haven't said.

Rules:
- One question only. Under 20 words.
- Don't repeat what they wrote. Point at what's missing.
- Use "you" — speak directly.
- Warm but honest. Never patronising.
""".trimIndent()

    val AUTO_TITLE = """
Generate a short, evocative title for a journal entry. The title should capture the
essence, not summarise the content. Think of it as the name you'd give this feeling.

Rules:
- 2-5 words only. No punctuation except a comma or dash.
- Never use "Reflection on" or "Thoughts about".
- Poetic but not pretentious.
- One title only. No alternatives, no explanation.
""".trimIndent()

    val GUIDED_QUESTION = """
You are guiding someone through a journaling session. You've seen their previous
answers in this conversation. Generate the next question that takes them one layer
deeper. Each question should build on what they revealed in their last answer.

Rules:
- One question only. Under 25 words.
- Never repeat a question already asked.
- Move from surface → feeling → meaning → what-now.
- Warm, curious, direct. No filler words.
""".trimIndent()

    val GUIDED_COMPILE = """
You are compiling a journal entry from a series of questions and answers. Weave the
answers into a single, cohesive first-person narrative. Preserve the person's voice
and specific words. Don't add insights they didn't express.

Rules:
- Write in first person. 1-3 short paragraphs.
- Keep their exact phrasing where possible.
- Don't add moral lessons or summaries.
- Natural, flowing prose — not a list of answers.
""".trimIndent()

    fun socraticUserPrompt(title: String, body: String): String =
        "Journal entry titled \"$title\":\n\n${body.take(1200)}"

    fun reframeUserPrompt(title: String, body: String): String =
        "Journal entry titled \"$title\":\n\n${body.take(1200)}"

    fun contextualStarterUserPrompt(moodLabel: String?, timeOfDay: String): String = buildString {
        appendLine("Context:")
        if (!moodLabel.isNullOrBlank()) appendLine("- Recent mood: $moodLabel")
        appendLine("- Time of day: $timeOfDay")
        appendLine("Generate a journaling prompt.")
    }

    fun goDeeperUserPrompt(bodySoFar: String): String =
        "Entry so far:\n\n${bodySoFar.take(800)}\n\nWhat question would pull them deeper?"

    fun autoTitleUserPrompt(body: String): String =
        "Journal entry:\n\n${body.take(600)}\n\nGenerate a title."

    fun guidedQuestionUserPrompt(exchanges: List<Pair<String, String>>, isFirst: Boolean): String =
        if (isFirst) {
            "This is the start of a guided journaling session. Ask the opening question."
        } else {
            buildString {
                appendLine("Previous exchanges:")
                exchanges.forEach { (q, a) ->
                    appendLine("Q: $q")
                    appendLine("A: $a")
                    appendLine()
                }
                appendLine("Generate the next question.")
            }
        }

    fun guidedCompileUserPrompt(exchanges: List<Pair<String, String>>): String = buildString {
        appendLine("Compile these exchanges into a journal entry:")
        appendLine()
        exchanges.forEach { (q, a) ->
            appendLine("Q: $q")
            appendLine("A: $a")
            appendLine()
        }
    }

    fun patternNarratorUserPrompt(
        avgValence: Double,
        topWords: List<Pair<String, Int>>,
        totalEntries: Int,
        totalMoods: Int,
        callback: String?,
        highlightSnippet: String,
    ): String = buildString {
        appendLine("Month summary:")
        appendLine("- $totalEntries journal entries, $totalMoods mood logs")
        val valenceWord = when {
            avgValence > 1.0 -> "predominantly positive"
            avgValence > 0.3 -> "leaning positive"
            avgValence > -0.3 -> "mixed, near neutral"
            avgValence > -1.0 -> "leaning difficult"
            else -> "predominantly heavy"
        }
        appendLine("- Overall emotional tone: $valenceWord (valence ${String.format("%.1f", avgValence)})")
        if (topWords.isNotEmpty()) {
            appendLine("- Words most on their mind: ${topWords.joinToString { "${it.first} (${it.second}x)" }}")
        }
        if (!callback.isNullOrBlank()) {
            appendLine("- Shift observed: $callback")
        }
        if (highlightSnippet.isNotBlank()) {
            appendLine("- Most invested entry excerpt: \"${highlightSnippet.take(200)}\"")
        }
    }
}
