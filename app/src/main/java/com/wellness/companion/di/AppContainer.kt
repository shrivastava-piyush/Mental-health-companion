package com.wellness.companion.di

import android.content.Context
import com.wellness.companion.data.db.WellnessDatabase
import com.wellness.companion.data.llm.LlamaEngine
import com.wellness.companion.data.llm.ModelManager
import com.wellness.companion.data.repository.JournalRepository
import com.wellness.companion.data.repository.MetricRepository
import com.wellness.companion.data.repository.MoodRepository
import com.wellness.companion.domain.llm.ReflectionEngine
import com.wellness.companion.domain.narrative.ColdOpenGenerator
import com.wellness.companion.domain.narrative.MirrorGenerator
import com.wellness.companion.domain.narrative.ThreadDetector

class AppContainer(context: Context) {

    private val appContext = context.applicationContext

    val database: WellnessDatabase by lazy { WellnessDatabase.get(appContext) }

    val moodRepository: MoodRepository by lazy { MoodRepository(database.moodDao()) }
    val metricRepository: MetricRepository by lazy { MetricRepository(database.metricDao()) }

    val threadDetector: ThreadDetector by lazy { ThreadDetector(database.narrativeDao()) }
    val coldOpenGenerator: ColdOpenGenerator by lazy {
        ColdOpenGenerator(database.narrativeDao(), database.moodDao())
    }
    val mirrorGenerator: MirrorGenerator by lazy {
        MirrorGenerator(database.moodDao(), database.journalDao(), database.narrativeDao())
    }

    val journalRepository: JournalRepository by lazy {
        JournalRepository(database.journalDao(), database.narrativeDao(), threadDetector)
    }

    val modelManager: ModelManager by lazy { ModelManager(appContext) }

    val llamaEngine: LlamaEngine by lazy { LlamaEngine(modelManager.modelPath()) }

    val reflectionEngine: ReflectionEngine? get() =
        if (modelManager.isDownloaded()) ReflectionEngine(llamaEngine) else null

    fun context(): Context = appContext
}
