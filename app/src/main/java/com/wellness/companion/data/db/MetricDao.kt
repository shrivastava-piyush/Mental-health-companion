package com.wellness.companion.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wellness.companion.data.db.entities.MetricEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface MetricDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: MetricEntry): Long

    @Query(
        """
        SELECT * FROM metric_entries
        WHERE type = :type AND createdAt BETWEEN :fromMillis AND :toMillis
        ORDER BY createdAt ASC
        """
    )
    fun observeType(type: String, fromMillis: Long, toMillis: Long): Flow<List<MetricEntry>>

    /** Latest-per-type summary, powers the dashboard rail. */
    @Query(
        """
        SELECT type, value, unit, createdAt
        FROM metric_entries
        WHERE id IN (
            SELECT MAX(id) FROM metric_entries GROUP BY type
        )
        """
    )
    fun observeLatestPerType(): Flow<List<MetricSnapshot>>

    @Query("DELETE FROM metric_entries WHERE id = :id")
    suspend fun deleteById(id: Long)
}

data class MetricSnapshot(
    val type: String,
    val value: Double,
    val unit: String,
    val createdAt: Long,
)
