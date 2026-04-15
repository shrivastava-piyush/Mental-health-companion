package com.wellness.companion.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.wellness.companion.data.db.JournalDao
import com.wellness.companion.data.db.JournalSummary
import com.wellness.companion.data.db.entities.JournalEntry
import kotlinx.coroutines.flow.Flow

class JournalRepository(private val dao: JournalDao) {

    /** Paged summaries – chunked at 30 to keep frame budget clean. */
    fun pagedSummaries(): Flow<PagingData<JournalSummary>> =
        Pager(
            config = PagingConfig(
                pageSize = 30,
                prefetchDistance = 15,
                initialLoadSize = 45,
                enablePlaceholders = false,
                maxSize = 240, // cap memory even on long-scroll sessions
            ),
            pagingSourceFactory = { dao.pagingSummaries() },
        ).flow

    fun observeById(id: Long): Flow<JournalEntry?> = dao.observeById(id)

    fun observeCount(): Flow<Int> = dao.observeCount()

    suspend fun save(entry: JournalEntry): Long =
        if (entry.id == 0L) dao.insert(entry)
        else entry.id.also { dao.update(entry) }

    suspend fun delete(id: Long) = dao.deleteById(id)
}
