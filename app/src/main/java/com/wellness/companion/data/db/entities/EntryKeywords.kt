package com.wellness.companion.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Cached keyword extraction for a journal entry. Computed once at save-time
 * so thread detection and cold-open matching never re-tokenise the body.
 */
@Entity(
    tableName = "entry_keywords",
    foreignKeys = [
        ForeignKey(
            entity = JournalEntry::class,
            parentColumns = ["id"],
            childColumns = ["entryId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["entryId"], unique = true, name = "idx_kw_entry"),
    ]
)
data class EntryKeywords(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "entryId")
    val entryId: Long,

    /** Comma-separated stemmed keywords in frequency order. */
    @ColumnInfo(name = "keywords")
    val keywords: String,
)
