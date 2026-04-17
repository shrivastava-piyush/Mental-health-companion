package com.wellness.companion.data.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migrations {

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS narrative_threads (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    label TEXT NOT NULL,
                    keywords TEXT NOT NULL,
                    createdAt INTEGER NOT NULL,
                    updatedAt INTEGER NOT NULL,
                    entryCount INTEGER NOT NULL DEFAULT 0,
                    status TEXT NOT NULL DEFAULT 'ongoing'
                )
                """.trimIndent()
            )
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_thread_updated_at ON narrative_threads(updatedAt)")

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS thread_entry_refs (
                    threadId INTEGER NOT NULL,
                    entryId INTEGER NOT NULL,
                    PRIMARY KEY(threadId, entryId),
                    FOREIGN KEY(threadId) REFERENCES narrative_threads(id) ON DELETE CASCADE,
                    FOREIGN KEY(entryId) REFERENCES journal_entries(id) ON DELETE CASCADE
                )
                """.trimIndent()
            )
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_ref_entry ON thread_entry_refs(entryId)")
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_ref_thread ON thread_entry_refs(threadId)")

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS entry_keywords (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    entryId INTEGER NOT NULL,
                    keywords TEXT NOT NULL,
                    FOREIGN KEY(entryId) REFERENCES journal_entries(id) ON DELETE CASCADE
                )
                """.trimIndent()
            )
            db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS idx_kw_entry ON entry_keywords(entryId)")
        }
    }

    val ALL: Array<Migration> = arrayOf(
        MIGRATION_1_2,
    )
}
