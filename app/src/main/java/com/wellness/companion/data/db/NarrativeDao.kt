package com.wellness.companion.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.wellness.companion.data.db.entities.EntryKeywords
import com.wellness.companion.data.db.entities.NarrativeThread
import com.wellness.companion.data.db.entities.ThreadEntryRef
import kotlinx.coroutines.flow.Flow

@Dao
interface NarrativeDao {

    // ── Threads ────────────────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThread(thread: NarrativeThread): Long

    @Update
    suspend fun updateThread(thread: NarrativeThread)

    @Query("SELECT * FROM narrative_threads ORDER BY updatedAt DESC")
    fun observeAllThreads(): Flow<List<NarrativeThread>>

    @Query("SELECT * FROM narrative_threads WHERE status = 'ongoing' ORDER BY updatedAt DESC LIMIT :limit")
    fun observeActiveThreads(limit: Int = 10): Flow<List<NarrativeThread>>

    @Query("SELECT * FROM narrative_threads WHERE id = :id")
    suspend fun threadById(id: Long): NarrativeThread?

    @Query("SELECT * FROM narrative_threads")
    suspend fun allThreads(): List<NarrativeThread>

    // ── Junction refs ──────────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRef(ref: ThreadEntryRef)

    @Query(
        """
        SELECT je.id, je.createdAt, je.updatedAt, je.title, je.wordCount
        FROM journal_entries je
        INNER JOIN thread_entry_refs r ON r.entryId = je.id
        WHERE r.threadId = :threadId
        ORDER BY je.createdAt DESC
        """
    )
    fun observeEntriesForThread(threadId: Long): Flow<List<JournalSummary>>

    @Query("SELECT COUNT(*) FROM thread_entry_refs WHERE threadId = :threadId")
    suspend fun entryCountForThread(threadId: Long): Int

    // ── Keywords cache ─────────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKeywords(kw: EntryKeywords)

    @Query("SELECT * FROM entry_keywords WHERE entryId = :entryId LIMIT 1")
    suspend fun keywordsForEntry(entryId: Long): EntryKeywords?

    @Query("SELECT * FROM entry_keywords")
    suspend fun allKeywords(): List<EntryKeywords>

    // ── Cold Open helpers ──────────────────────────────────────────────────

    /** Entries from roughly the same calendar day in previous months. */
    @Query(
        """
        SELECT id, createdAt, updatedAt, title, body, wordCount
        FROM journal_entries
        WHERE abs((createdAt / 86400000) % 30 - (:nowMillis / 86400000) % 30) <= 1
          AND id != :excludeId
        ORDER BY createdAt DESC
        LIMIT :limit
        """
    )
    suspend fun entriesNearSameDayOfMonth(nowMillis: Long, excludeId: Long = -1, limit: Int = 5): List<ColdOpenCandidate>

    /** Most recent entries for keyword-similarity matching. */
    @Query(
        """
        SELECT je.id, je.createdAt, je.title, je.body, ek.keywords
        FROM journal_entries je
        INNER JOIN entry_keywords ek ON ek.entryId = je.id
        WHERE je.createdAt < :beforeMillis
        ORDER BY je.createdAt DESC
        LIMIT :limit
        """
    )
    suspend fun recentEntriesWithKeywords(beforeMillis: Long, limit: Int = 30): List<KeywordedEntry>
}

data class ColdOpenCandidate(
    val id: Long,
    val createdAt: Long,
    val updatedAt: Long,
    val title: String,
    val body: String,
    val wordCount: Int,
)

data class KeywordedEntry(
    val id: Long,
    val createdAt: Long,
    val title: String,
    val body: String,
    val keywords: String,
)
