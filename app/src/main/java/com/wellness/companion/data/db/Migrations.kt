package com.wellness.companion.data.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Central migration registry. Every new release appends to [ALL] so that
 * users upgrading across multiple versions never regenerate their local
 * database (which, at 100k+ rows, would stall the UI for seconds).
 *
 * Template for the next migration:
 *
 *   private val MIGRATION_1_2 = object : Migration(1, 2) {
 *       override fun migrate(db: SupportSQLiteDatabase) {
 *           db.execSQL("ALTER TABLE mood_entries ADD COLUMN triggers TEXT NOT NULL DEFAULT ''")
 *           db.execSQL("CREATE INDEX IF NOT EXISTS idx_mood_triggers ON mood_entries(triggers)")
 *       }
 *   }
 */
object Migrations {

    @Suppress("unused")
    private val MIGRATION_EXAMPLE = object : Migration(100, 101) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Illustrative only – never added to ALL.
        }
    }

    val ALL: Array<Migration> = arrayOf(
        // Real migrations appended here as the schema evolves.
    )
}
