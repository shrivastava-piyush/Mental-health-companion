package com.wellness.companion.di

import android.content.Context
import com.wellness.companion.data.db.WellnessDatabase
import com.wellness.companion.data.repository.JournalRepository
import com.wellness.companion.data.repository.MetricRepository
import com.wellness.companion.data.repository.MoodRepository

/**
 * Lightweight, manual DI container.
 *
 * Rationale: Hilt / Dagger add an annotation-processor step and 200–400 KB of
 * generated code. For a tree of ~3 repositories and a handful of use-cases a
 * lazy container keeps cold-start under 300 ms and the APK trim.
 *
 * Held by [com.wellness.companion.WellnessApp] for the process lifetime;
 * ViewModels reach into it via a [androidx.lifecycle.viewmodel.CreationExtras]
 * factory – there is no service-locator call from deep in the UI tree.
 */
class AppContainer(context: Context) {

    private val appContext = context.applicationContext

    val database: WellnessDatabase by lazy { WellnessDatabase.get(appContext) }

    val moodRepository: MoodRepository by lazy { MoodRepository(database.moodDao()) }
    val journalRepository: JournalRepository by lazy { JournalRepository(database.journalDao()) }
    val metricRepository: MetricRepository by lazy { MetricRepository(database.metricDao()) }

    /** Exposed for screens that need a raw application [Context] (e.g. biometrics). */
    fun context(): Context = appContext
}
