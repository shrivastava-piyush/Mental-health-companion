package com.wellness.companion.ui.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wellness.companion.data.db.JournalSummary
import com.wellness.companion.data.db.entities.NarrativeThread
import com.wellness.companion.data.repository.JournalRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class JournalListViewModel(
    private val repo: JournalRepository,
) : ViewModel() {

    data class UiState(
        val entries: List<JournalSummary> = emptyList(),
        val threads: List<NarrativeThread> = emptyList()
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repo.observeSummaries(),
                repo.observeActiveThreads()
            ) { entries, threads ->
                UiState(entries, threads)
            }.collect { next ->
                _state.value = next
            }
        }
    }
}
