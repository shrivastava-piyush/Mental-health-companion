package com.wellness.companion.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Physiological daily metric (sleep hours, activity minutes, hydration, etc.).
 *
 * A composite (type, createdAt) index supports the common access pattern
 * "give me the last N rows of type=SLEEP", which otherwise triggers a
 * full scan over the ~100k-row table.
 */
@Entity(
    tableName = "metric_entries",
    indices = [
        Index(value = ["type", "createdAt"], name = "idx_metric_type_time"),
        Index(value = ["createdAt"], name = "idx_metric_created_at"),
    ]
)
data class MetricEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "createdAt")
    val createdAt: Long,

    /** Enum name – INTEGER would be denser but TEXT keeps migrations painless. */
    @ColumnInfo(name = "type")
    val type: String,

    /** Stored as REAL so "7.5 hours of sleep" or "0.8 L hydration" both fit. */
    @ColumnInfo(name = "value")
    val value: Double,

    /** Unit string for display ("h", "min", "L", ...). */
    @ColumnInfo(name = "unit")
    val unit: String,
)

enum class MetricType {
    SLEEP_HOURS,
    ACTIVITY_MINUTES,
    HYDRATION_LITRES,
    STEPS,
    MEDITATION_MINUTES,
    ;

    val displayName: String
        get() = when (this) {
            SLEEP_HOURS -> "Sleep"
            ACTIVITY_MINUTES -> "Activity"
            HYDRATION_LITRES -> "Hydration"
            STEPS -> "Steps"
            MEDITATION_MINUTES -> "Meditation"
        }

    val unit: String
        get() = when (this) {
            SLEEP_HOURS -> "h"
            ACTIVITY_MINUTES -> "min"
            HYDRATION_LITRES -> "L"
            STEPS -> ""
            MEDITATION_MINUTES -> "min"
        }
}
