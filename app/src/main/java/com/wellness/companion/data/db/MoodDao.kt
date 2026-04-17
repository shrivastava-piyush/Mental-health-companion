package com.wellness.companion.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wellness.companion.data.db.entities.MoodEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: MoodEntry): Long

    /** Paged stream for the history list – observes and diff-animates. */
    @Query(
        """
        SELECT * FROM mood_entries
        WHERE createdAt BETWEEN :fromMillis AND :toMillis
        ORDER BY createdAt DESC
        """
    )
    fun observeRange(fromMillis: Long, toMillis: Long): Flow<List<MoodEntry>>

    /** Daily aggregate buckets – computed in SQL so the UI receives ~30 rows. */
    @Query(
        """
        SELECT
            (createdAt / 86400000) AS dayBucket,
            AVG(valence)           AS avgValence,
            AVG(arousal)           AS avgArousal,
            COUNT(*)               AS sampleCount
        FROM mood_entries
        WHERE createdAt BETWEEN :fromMillis AND :toMillis
        GROUP BY dayBucket
        ORDER BY dayBucket ASC
        """
    )
    fun observeDailyAggregate(fromMillis: Long, toMillis: Long): Flow<List<DailyMoodBucket>>

    @Query("SELECT COUNT(*) FROM mood_entries")
    fun observeCount(): Flow<Int>

    @Query("DELETE FROM mood_entries WHERE id = :id")
    suspend fun deleteById(id: Long)

    /** Snapshot (non-Flow) daily aggregate for MirrorGenerator. */
    @Query(
        """
        SELECT
            (createdAt / 86400000) AS dayBucket,
            AVG(valence)           AS avgValence,
            AVG(arousal)           AS avgArousal,
            COUNT(*)               AS sampleCount
        FROM mood_entries
        WHERE createdAt BETWEEN :fromMillis AND :toMillis
        GROUP BY dayBucket
        ORDER BY dayBucket ASC
        """
    )
    suspend fun observeDailyAggregateSnapshot(fromMillis: Long, toMillis: Long): List<DailyMoodBucket>

    /** Most recent mood labels for cold-open keyword seeding. */
    @Query("SELECT label FROM mood_entries ORDER BY createdAt DESC LIMIT :limit")
    suspend fun recentLabels(limit: Int): List<String>

    /** Average valence over a range — used by MirrorGenerator. */
    @Query("SELECT AVG(valence) FROM mood_entries WHERE createdAt BETWEEN :from AND :to")
    suspend fun avgValence(from: Long, to: Long): Double?
}

/** Projection class – lives with the DAO that materialises it. */
data class DailyMoodBucket(
    val dayBucket: Long,
    val avgValence: Double,
    val avgArousal: Double,
    val sampleCount: Int,
)
