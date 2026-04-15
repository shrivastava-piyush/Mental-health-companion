package com.wellness.companion.data.repository

import com.wellness.companion.data.db.MetricDao
import com.wellness.companion.data.db.MetricSnapshot
import com.wellness.companion.data.db.entities.MetricEntry
import com.wellness.companion.data.db.entities.MetricType
import kotlinx.coroutines.flow.Flow

class MetricRepository(private val dao: MetricDao) {

    fun observeType(type: MetricType, fromMillis: Long, toMillis: Long): Flow<List<MetricEntry>> =
        dao.observeType(type.name, fromMillis, toMillis)

    fun observeLatestPerType(): Flow<List<MetricSnapshot>> = dao.observeLatestPerType()

    suspend fun log(type: MetricType, value: Double, atMillis: Long = System.currentTimeMillis()): Long =
        dao.insert(
            MetricEntry(
                createdAt = atMillis,
                type = type.name,
                value = value,
                unit = type.unit,
            )
        )
}
