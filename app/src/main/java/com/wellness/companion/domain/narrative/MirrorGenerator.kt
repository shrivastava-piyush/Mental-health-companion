package com.wellness.companion.domain.narrative

import com.wellness.companion.data.db.DailyMoodBucket
import com.wellness.companion.data.db.MoodDao
import com.wellness.companion.data.db.NarrativeDao
import com.wellness.companion.data.db.entities.JournalEntry
import com.wellness.companion.data.db.JournalDao
import com.wellness.companion.domain.nlp.TextAnalyzer

/**
 * Generates the monthly "Mirror Moment" — a single-card synthesis of the
 * user's emotional arc, top words, and a highlighted sentence.
 *
 * Designed to run on IO; the UI observes a cached result via StateFlow.
 */
class MirrorGenerator(
    private val moodDao: MoodDao,
    private val journalDao: JournalDao,
    private val narrativeDao: NarrativeDao,
) {
    data class Mirror(
        val periodLabel: String,
        val moodArc: List<DailyMoodBucket>,
        val avgValence: Double,
        val topWords: List<Pair<String, Int>>,
        val highlightTitle: String,
        val highlightSnippet: String,
        val callback: String?,
        val totalEntries: Int,
        val totalMoods: Int,
    )

    suspend fun generate(fromMillis: Long, toMillis: Long, periodLabel: String): Mirror? {
        val entries = narrativeDao.recentEntriesWithKeywords(toMillis, limit = 200)
            .filter { it.createdAt in fromMillis..toMillis }

        if (entries.isEmpty()) return null

        // Mood arc for the sparkline.
        val arc = moodDao.observeDailyAggregateSnapshot(fromMillis, toMillis)
        val avg = moodDao.avgValence(fromMillis, toMillis) ?: 0.0

        // Top words across all entries in the period.
        val allText = entries.joinToString(" ") { it.body }
        val topWords = TextAnalyzer.topWords(allText, limit = 5)

        // Highlight: the longest entry (most emotionally invested).
        val highlight = entries.maxByOrNull { it.body.length }

        // Callback: compare first-week keywords vs last-week keywords.
        val midpoint = fromMillis + (toMillis - fromMillis) / 2
        val firstHalf = entries.filter { it.createdAt < midpoint }
        val secondHalf = entries.filter { it.createdAt >= midpoint }
        val callback = buildCallback(firstHalf, secondHalf)

        return Mirror(
            periodLabel = periodLabel,
            moodArc = arc,
            avgValence = avg,
            topWords = topWords,
            highlightTitle = highlight?.title ?: "",
            highlightSnippet = highlight?.body?.take(160)?.trim() ?: "",
            callback = callback,
            totalEntries = entries.size,
            totalMoods = arc.sumOf { it.sampleCount },
        )
    }

    private fun buildCallback(
        firstHalf: List<com.wellness.companion.data.db.KeywordedEntry>,
        secondHalf: List<com.wellness.companion.data.db.KeywordedEntry>,
    ): String? {
        if (firstHalf.isEmpty() || secondHalf.isEmpty()) return null
        val firstKws = firstHalf.flatMap { it.keywords.split(",") }.filter { it.isNotBlank() }.toSet()
        val secondKws = secondHalf.flatMap { it.keywords.split(",") }.filter { it.isNotBlank() }.toSet()
        val dropped = firstKws - secondKws
        val appeared = secondKws - firstKws
        return when {
            dropped.size >= 2 -> "You started the month writing about ${dropped.take(2).joinToString(" and ")}. By mid-month, you'd moved on."
            appeared.size >= 2 -> "New themes appeared: ${appeared.take(2).joinToString(" and ")}."
            else -> null
        }
    }
}
