package com.wellness.companion.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.wellness.companion.ui.home.HomeViewModel
import com.wellness.companion.ui.insights.InsightsViewModel
import com.wellness.companion.ui.journal.JournalEditorViewModel
import com.wellness.companion.ui.journal.JournalListViewModel
import com.wellness.companion.ui.journal.ThreadDetailViewModel
import com.wellness.companion.ui.mood.MoodViewModel

object ViewModelFactories {

    fun home(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
        initializer { 
            HomeViewModel(
                container.moodRepository, 
                container.journalRepository, 
                container.reflectionEngine
            ) 
        }
    }

    fun mood(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
        initializer { MoodViewModel(container.moodRepository, container.metricRepository) }
    }

    fun journalList(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
        initializer { JournalListViewModel(container.journalRepository) }
    }

    fun journalEditor(container: AppContainer, entryId: Long): ViewModelProvider.Factory =
        viewModelFactory {
            initializer {
                JournalEditorViewModel(
                    container.journalRepository,
                    container.coldOpenGenerator,
                    container.reflectionEngine,
                    container.database.moodDao(),
                    entryId,
                )
            }
        }

    fun threadDetail(container: AppContainer, threadId: Long): ViewModelProvider.Factory =
        viewModelFactory {
            initializer { ThreadDetailViewModel(container.journalRepository, threadId) }
        }

    fun insights(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
        initializer {
            InsightsViewModel(
                container.moodRepository,
                container.metricRepository,
                container.journalRepository,
                container.mirrorGenerator,
                container.reflectionEngine,
            )
        }
    }
}
