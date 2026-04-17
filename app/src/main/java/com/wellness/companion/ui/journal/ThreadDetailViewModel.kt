package com.wellness.companion.ui.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wellness.companion.data.db.JournalSummary
import com.wellness.companion.data.repository.JournalRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ThreadDetailViewModel(
    repo: JournalRepository,
    threadId: Long,
) : ViewModel() {

    val entries: StateFlow<List<JournalSummary>> =
        repo.observeEntriesForThread(threadId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
