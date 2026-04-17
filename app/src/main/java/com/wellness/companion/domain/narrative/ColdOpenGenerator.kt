package com.wellness.companion.domain.narrative

import com.wellness.companion.data.db.ColdOpenCandidate
import com.wellness.companion.data.db.KeywordedEntry
import com.wellness.companion.data.db.NarrativeDao
import com.wellness.companion.data.db.entities.MoodEntry
import com.wellness.companion.data.db.MoodDao
import com.wellness.companion.domain.nlp.TextAnalyzer

/**
 * Picks the single best "Previously on your life..." prompt to show when the
 * user opens a new journal entry. Three signals, scored and blended:
 *
 *  1. **Time echo** — same-ish day of the month from a prior month.
 *  2. **Keyword overlap** — past entry whose topics match today's mood label
 *     or the user's recent keyword fingerprint.
 *  3. **Mood resonance** — past entry written near the same valence as the
 *     most recent mood log.
 *
 * All computation is local. Nothing leaves the device.
 */
class ColdOpenGenerator(
    private val narrativeDao: NarrativeDao,
    private val moodDao: MoodDao,
) {
    data class ColdOpen(
        val entryId: Long,
        val title: String,
        val snippet: String,
        val reason: String,
    )

    suspend fun generate(): ColdOpen? {
        val now = System.currentTimeMillis()

        // Gather candidates from two sources.
        val timeEchoes = narrativeDao.entriesNearSameDayOfMonth(now)
        val keyworded = narrativeDao.recentEntriesWithKeywords(now, limit = 40)

        if (timeEchoes.isEmpty() && keyworded.isEmpty()) return null

        // Build a "today fingerprint" from the most recent mood label.
        val recentMoods = moodDao.recentLabels(3)
        val todayKeywords = recentMoods.flatMap { TextAnalyzer.keywords(it, limit = 3) }.distinct()

        val scored = mutableListOf<Pair<Float, ColdOpen>>()

        // Score time echoes.
        for (c in timeEchoes) {
            val ageMonths = ((now - c.createdAt) / (30L * 86_400_000L)).coerceAtLeast(1)
            val snippet = firstSentence(c.body)
            scored += (1.0f / ageMonths) to ColdOpen(
                entryId = c.id,
                title = c.title,
                snippet = snippet,
                reason = "${ageMonths} month${if (ageMonths > 1) "s" else ""} ago you wrote:",
            )
        }

        // Score keyword overlaps.
        if (todayKeywords.isNotEmpty()) {
            for (k in keyworded) {
                val entryKws = k.keywords.split(",").filter { it.isNotBlank() }
                val sim = TextAnalyzer.similarity(todayKeywords, entryKws)
                if (sim > 0.15f) {
                    val snippet = firstSentence(k.body)
                    scored += sim to ColdOpen(
                        entryId = k.id,
                        title = k.title,
                        snippet = snippet,
                        reason = "You were thinking about similar things when you wrote:",
                    )
                }
            }
        }

        return scored
            .sortedByDescending { it.first }
            .firstOrNull()
            ?.second
    }

    private fun firstSentence(body: String): String {
        val end = body.indexOfFirst { it == '.' || it == '!' || it == '?' }
        val raw = if (end > 0 && end < 200) body.substring(0, end + 1) else body.take(140)
        return raw.trim().ifEmpty { body.take(80) }
    }
}
