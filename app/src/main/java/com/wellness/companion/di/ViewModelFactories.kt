package com.wellness.companion.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.wellness.companion.ui.insights.InsightsViewModel
import com.wellness.companion.ui.journal.JournalEditorViewModel
import com.wellness.companion.ui.journal.JournalListViewModel
import com.wellness.companion.ui.mood.MoodViewModel

/**
 * Single place that turns [AppContainer] into [ViewModelProvider.Factory]s.
 *
 * Using [viewModelFactory] avoids writing five bespoke factories and plays
 * nicely with `viewModel()` in Compose, e.g.:
 *
 *   val vm: MoodViewModel = viewModel(factory = ViewModelFactories.mood(container))
 */
object ViewModelFactories {

    fun mood(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
        initializer { MoodViewModel(container.moodRepository, container.metricRepository) }
    }

    fun journalList(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
        initializer { JournalListViewModel(container.journalRepository) }
    }

    fun journalEditor(container: AppContainer, entryId: Long): ViewModelProvider.Factory =
        viewModelFactory {
            initializer { JournalEditorViewModel(container.journalRepository, entryId) }
        }

    fun insights(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
        initializer {
            InsightsViewModel(
                container.moodRepository,
                container.metricRepository,
                container.journalRepository,
            )
        }
    }

}
