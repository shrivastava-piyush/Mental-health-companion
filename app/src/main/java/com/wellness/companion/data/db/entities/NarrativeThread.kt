package com.wellness.companion.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * An auto-detected narrative arc across journal entries. Examples:
 * "the new job", "marathon training", "sleep struggle".
 *
 * Threads are created and updated by [ThreadDetector] at save-time.
 * Users never manually create them — they emerge from recurring keywords.
 */
@Entity(
    tableName = "narrative_threads",
    indices = [
        Index(value = ["updatedAt"], name = "idx_thread_updated_at"),
    ]
)
data class NarrativeThread(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** Human-readable label, e.g. "the new job". */
    @ColumnInfo(name = "label")
    val label: String,

    /** Comma-separated stemmed keywords that define this thread. */
    @ColumnInfo(name = "keywords")
    val keywords: String,

    @ColumnInfo(name = "createdAt")
    val createdAt: Long,

    @ColumnInfo(name = "updatedAt")
    val updatedAt: Long,

    /** Number of journal entries linked to this thread. Denormalised for fast list display. */
    @ColumnInfo(name = "entryCount")
    val entryCount: Int = 0,

    /** "ongoing", "resolved", "dormant". Plain string for migration friendliness. */
    @ColumnInfo(name = "status", defaultValue = "ongoing")
    val status: String = "ongoing",
)
