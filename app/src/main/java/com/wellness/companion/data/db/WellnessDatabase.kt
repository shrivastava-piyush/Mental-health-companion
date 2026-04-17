package com.wellness.companion.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.wellness.companion.data.db.entities.EntryKeywords
import com.wellness.companion.data.db.entities.JournalEntry
import com.wellness.companion.data.db.entities.MetricEntry
import com.wellness.companion.data.db.entities.MoodEntry
import com.wellness.companion.data.db.entities.NarrativeThread
import com.wellness.companion.data.db.entities.ThreadEntryRef

@Database(
    entities = [
        MoodEntry::class,
        JournalEntry::class,
        MetricEntry::class,
        NarrativeThread::class,
        ThreadEntryRef::class,
        EntryKeywords::class,
    ],
    version = 2,
    exportSchema = true,
)
abstract class WellnessDatabase : RoomDatabase() {

    abstract fun moodDao(): MoodDao
    abstract fun journalDao(): JournalDao
    abstract fun metricDao(): MetricDao
    abstract fun narrativeDao(): NarrativeDao

    companion object {
        @Volatile private var INSTANCE: WellnessDatabase? = null

        fun get(context: Context): WellnessDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: build(context).also { INSTANCE = it }
            }

        private fun build(context: Context): WellnessDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                WellnessDatabase::class.java,
                "wellness.db",
            )
                // WAL is default; we additionally tell SQLite to reduce synchronous
                // writes slightly in favour of throughput – safe for journal data.
                .setJournalMode(JournalMode.WRITE_AHEAD_LOGGING)
                .addMigrations(*Migrations.ALL)
                .addCallback(object : Callback() {
                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        // PRAGMA tunings for the 100k+ row target.
                        db.execSQL("PRAGMA synchronous = NORMAL")
                        db.execSQL("PRAGMA temp_store = MEMORY")
                        db.execSQL("PRAGMA mmap_size = 67108864") // 64 MB memory map
                    }
                })
                .build()
    }
}
