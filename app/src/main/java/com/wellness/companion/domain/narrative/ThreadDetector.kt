package com.wellness.companion.domain.narrative

import com.wellness.companion.data.db.NarrativeDao
import com.wellness.companion.data.db.entities.EntryKeywords
import com.wellness.companion.data.db.entities.NarrativeThread
import com.wellness.companion.data.db.entities.ThreadEntryRef
import com.wellness.companion.domain.nlp.TextAnalyzer

/**
 * Runs at journal-save time (on IO dispatcher). Extracts keywords from the
 * saved entry, matches against existing threads, and either links the entry
 * to a thread or creates a new one if there are ≥ 2 entries with high overlap.
 *
 * Typical runtime: 5–15 ms on a corpus of 50 threads × 20 keywords each.
 */
class ThreadDetector(private val dao: NarrativeDao) {

    suspend fun process(entryId: Long, title: String, body: String) {
        val combined = "$title $body"
        val keywords = TextAnalyzer.keywords(combined, limit = 15)
        if (keywords.isEmpty()) return

        val keywordCsv = keywords.joinToString(",")

        // Persist keyword cache for this entry.
        dao.insertKeywords(EntryKeywords(entryId = entryId, keywords = keywordCsv))

        // Try to match existing threads.
        val threads = dao.allThreads()
        val now = System.currentTimeMillis()

        var matched = false
        for (thread in threads) {
            val threadKws = thread.keywords.split(",").filter { it.isNotBlank() }
            val sim = TextAnalyzer.similarity(keywords, threadKws)
            if (sim >= MATCH_THRESHOLD) {
                dao.insertRef(ThreadEntryRef(threadId = thread.id, entryId = entryId))
                val count = dao.entryCountForThread(thread.id)
                dao.updateThread(
                    thread.copy(
                        updatedAt = now,
                        entryCount = count,
                        status = "ongoing",
                    )
                )
                matched = true
            }
        }

        // If no match, check if this entry + any unthreaded entry form a new thread.
        if (!matched) {
            tryCreateThread(entryId, keywords, keywordCsv, now)
        }

        // Mark threads with no new entries in 30 days as dormant.
        val dormantCutoff = now - 30L * 86_400_000L
        for (thread in threads) {
            if (thread.status == "ongoing" && thread.updatedAt < dormantCutoff) {
                dao.updateThread(thread.copy(status = "dormant"))
            }
        }
    }

    private suspend fun tryCreateThread(
        entryId: Long,
        keywords: List<String>,
        keywordCsv: String,
        now: Long,
    ) {
        val allKws = dao.allKeywords()
        for (other in allKws) {
            if (other.entryId == entryId) continue
            val otherKws = other.keywords.split(",").filter { it.isNotBlank() }
            val sim = TextAnalyzer.similarity(keywords, otherKws)
            if (sim >= CREATE_THRESHOLD) {
                // Shared keywords become the thread label.
                val shared = keywords.toSet().intersect(otherKws.toSet())
                val label = shared.take(3).joinToString(" & ")
                    .ifBlank { keywords.first() }

                val threadId = dao.insertThread(
                    NarrativeThread(
                        label = label,
                        keywords = keywordCsv,
                        createdAt = now,
                        updatedAt = now,
                        entryCount = 2,
                        status = "ongoing",
                    )
                )
                dao.insertRef(ThreadEntryRef(threadId = threadId, entryId = other.entryId))
                dao.insertRef(ThreadEntryRef(threadId = threadId, entryId = entryId))
                return
            }
        }
    }

    companion object {
        private const val MATCH_THRESHOLD = 0.20f
        private const val CREATE_THRESHOLD = 0.25f
    }
}
