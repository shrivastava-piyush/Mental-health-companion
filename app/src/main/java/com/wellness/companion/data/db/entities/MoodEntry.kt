package com.wellness.companion.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A single mood/emotion log.
 *
 * Indexing strategy for 100k+ rows:
 *  - [createdAt] is indexed because every trend query filters and sorts by time.
 *  - [valence] is indexed because the insights aggregate by mood family.
 *  - Columns stored as primitives (INTEGER / REAL / TEXT) only; no blobs.
 */
@Entity(
    tableName = "mood_entries",
    indices = [
        Index(value = ["createdAt"], name = "idx_mood_created_at"),
        Index(value = ["valence"], name = "idx_mood_valence"),
    ]
)
data class MoodEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** Epoch millis. Indexed for range queries ("last 30 days"). */
    @ColumnInfo(name = "createdAt")
    val createdAt: Long,

    /** Valence -2..+2 (very low → very high). Indexed for bucketing. */
    @ColumnInfo(name = "valence")
    val valence: Int,

    /** Arousal 0..4 (calm → energised). */
    @ColumnInfo(name = "arousal")
    val arousal: Int,

    /** Free-form single-word tag ("anxious", "grateful", ...). */
    @ColumnInfo(name = "label")
    val label: String,

    /** Optional short note, capped at ~280 chars at the UI layer. */
    @ColumnInfo(name = "note", defaultValue = "")
    val note: String = "",
)
