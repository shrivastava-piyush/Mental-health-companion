package com.wellness.companion.data.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.wellness.companion.data.db.entities.JournalEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: JournalEntry): Long

    @Update
    suspend fun update(entry: JournalEntry)

    @Query(
        """
        SELECT id, createdAt, updatedAt, title, wordCount
        FROM journal_entries
        ORDER BY createdAt DESC
        """
    )
    fun pagingSummaries(): PagingSource<Int, JournalSummary>

    @Query(
        """
        SELECT id, createdAt, updatedAt, title, wordCount
        FROM journal_entries
        ORDER BY createdAt DESC
        """
    )
    fun observeSummaries(): Flow<List<JournalSummary>>

    @Query("SELECT * FROM journal_entries WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<JournalEntry?>

    @Query("SELECT COUNT(*) FROM journal_entries")
    fun observeCount(): Flow<Int>

    @Query("DELETE FROM journal_entries WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM journal_entries ORDER BY createdAt DESC LIMIT :limit")
    suspend fun fetchRecentEntries(limit: Int): List<JournalEntry>
}

data class JournalSummary(
    val id: Long,
    val createdAt: Long,
    val updatedAt: Long,
    val title: String,
    val wordCount: Int,
)
