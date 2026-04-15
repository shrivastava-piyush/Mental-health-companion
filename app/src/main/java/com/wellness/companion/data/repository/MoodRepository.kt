package com.wellness.companion.data.repository

import com.wellness.companion.data.db.DailyMoodBucket
import com.wellness.companion.data.db.MoodDao
import com.wellness.companion.data.db.entities.MoodEntry
import kotlinx.coroutines.flow.Flow

class MoodRepository(private val dao: MoodDao) {

    fun observeRange(fromMillis: Long, toMillis: Long): Flow<List<MoodEntry>> =
        dao.observeRange(fromMillis, toMillis)

    fun observeDailyAggregate(fromMillis: Long, toMillis: Long): Flow<List<DailyMoodBucket>> =
        dao.observeDailyAggregate(fromMillis, toMillis)

    fun observeCount(): Flow<Int> = dao.observeCount()

    suspend fun log(entry: MoodEntry): Long = dao.insert(entry)

    suspend fun delete(id: Long) = dao.deleteById(id)
}
