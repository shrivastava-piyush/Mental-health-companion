package com.wellness.companion.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.wellness.companion.data.db.JournalDao
import com.wellness.companion.data.db.JournalSummary
import com.wellness.companion.data.db.NarrativeDao
import com.wellness.companion.data.db.entities.JournalEntry
import com.wellness.companion.data.db.entities.NarrativeThread
import com.wellness.companion.domain.narrative.ThreadDetector
import kotlinx.coroutines.flow.Flow
import androidx.room.Query

class JournalRepository(
    private val dao: JournalDao,
    private val narrativeDao: NarrativeDao,
    private val threadDetector: ThreadDetector,
) {
    fun pagedSummaries(): Flow<PagingData<JournalSummary>> =
        Pager(
            config = PagingConfig(
                pageSize = 30,
                prefetchDistance = 15,
                initialLoadSize = 45,
                enablePlaceholders = false,
                maxSize = 240,
            ),
            pagingSourceFactory = { dao.pagingSummaries() },
        ).flow

    // For Home screen preview
    fun observeSummaries(): Flow<List<JournalSummary>> = 
        // We'll need to add this to JournalDao for Home screen
        // For now, let's keep it consistent.
        dao.observeSummaries()

    fun observeById(id: Long): Flow<JournalEntry?> = dao.observeById(id)

    fun observeCount(): Flow<Int> = dao.observeCount()

    /** Save entry and run thread detection in the same suspend call. */
    suspend fun save(entry: JournalEntry): Long {
        val id = if (entry.id == 0L) dao.insert(entry) else entry.id.also { dao.update(entry) }
        threadDetector.process(id, entry.title, entry.body)
        return id
    }

    suspend fun delete(id: Long) = dao.deleteById(id)

    // ── Narrative queries exposed to UI ────────────────────────────────

    fun observeActiveThreads(): Flow<List<NarrativeThread>> =
        narrativeDao.observeActiveThreads()

    fun observeAllThreads(): Flow<List<NarrativeThread>> =
        narrativeDao.observeAllThreads()

    fun observeEntriesForThread(threadId: Long): Flow<List<JournalSummary>> =
        narrativeDao.observeEntriesForThread(threadId)
}
