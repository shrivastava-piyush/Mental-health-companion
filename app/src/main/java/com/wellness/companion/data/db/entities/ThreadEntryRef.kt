package com.wellness.companion.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Junction table linking [NarrativeThread] ↔ [JournalEntry].
 *
 * Composite primary key prevents duplicates. Foreign keys cascade deletes
 * so removing a journal entry or thread cleans up automatically.
 */
@Entity(
    tableName = "thread_entry_refs",
    primaryKeys = ["threadId", "entryId"],
    foreignKeys = [
        ForeignKey(
            entity = NarrativeThread::class,
            parentColumns = ["id"],
            childColumns = ["threadId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = JournalEntry::class,
            parentColumns = ["id"],
            childColumns = ["entryId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["entryId"], name = "idx_ref_entry"),
        Index(value = ["threadId"], name = "idx_ref_thread"),
    ]
)
data class ThreadEntryRef(
    @ColumnInfo(name = "threadId")
    val threadId: Long,

    @ColumnInfo(name = "entryId")
    val entryId: Long,
)
