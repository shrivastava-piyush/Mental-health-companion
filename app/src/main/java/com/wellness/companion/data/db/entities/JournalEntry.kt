package com.wellness.companion.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A private, on-device-only journal entry.
 *
 * Paging 3 drives the list, so the only mandatory index is [createdAt]
 * (DESC reads). [wordCount] is indexed to power quick "long-form vs quick
 * notes" analytics without scanning the body TEXT blob.
 */
@Entity(
    tableName = "journal_entries",
    indices = [
        Index(value = ["createdAt"], name = "idx_journal_created_at"),
        Index(value = ["wordCount"], name = "idx_journal_word_count"),
    ]
)
data class JournalEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "createdAt")
    val createdAt: Long,

    @ColumnInfo(name = "updatedAt")
    val updatedAt: Long,

    @ColumnInfo(name = "title")
    val title: String,

    /** Body text. Kept in the same row for simpler paging & atomic writes. */
    @ColumnInfo(name = "body")
    val body: String,

    /** Precomputed at write-time so list rows never touch [body]. */
    @ColumnInfo(name = "wordCount")
    val wordCount: Int,
)
