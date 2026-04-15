package com.wellness.companion.ui.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wellness.companion.data.db.DailyMoodBucket
import com.wellness.companion.data.db.MetricSnapshot
import com.wellness.companion.data.repository.JournalRepository
import com.wellness.companion.data.repository.MetricRepository
import com.wellness.companion.data.repository.MoodRepository
import com.wellness.companion.domain.Time
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class InsightsViewModel(
    mood: MoodRepository,
    metric: MetricRepository,
    journal: JournalRepository,
) : ViewModel() {

    data class UiState(
        val trend: List<DailyMoodBucket> = emptyList(),
        val metrics: List<MetricSnapshot> = emptyList(),
        val totalMoods: Int = 0,
        val totalJournals: Int = 0,
    )

    val state: StateFlow<UiState> = combine(
        mood.observeDailyAggregate(Time.daysAgoMillis(30), Long.MAX_VALUE),
        metric.observeLatestPerType(),
        mood.observeCount(),
        journal.observeCount(),
    ) { trend, metrics, totalMoods, totalJournals ->
        UiState(trend, metrics, totalMoods, totalJournals)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        UiState(),
    )
}
