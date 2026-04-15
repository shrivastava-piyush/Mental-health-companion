package com.wellness.companion.ui.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.wellness.companion.data.db.JournalSummary
import com.wellness.companion.data.repository.JournalRepository
import kotlinx.coroutines.flow.Flow

class JournalListViewModel(
    repo: JournalRepository,
) : ViewModel() {
    val pager: Flow<PagingData<JournalSummary>> =
        repo.pagedSummaries().cachedIn(viewModelScope)
}
