package com.wellness.companion.ui.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wellness.companion.data.db.DailyMoodBucket
import com.wellness.companion.data.db.MetricSnapshot
import com.wellness.companion.data.repository.JournalRepository
import com.wellness.companion.data.repository.MetricRepository
import com.wellness.companion.data.repository.MoodRepository
import com.wellness.companion.domain.Time
import com.wellness.companion.domain.narrative.MirrorGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class InsightsViewModel(
    mood: MoodRepository,
    metric: MetricRepository,
    journal: JournalRepository,
    private val mirrorGen: MirrorGenerator,
) : ViewModel() {

    data class UiState(
        val trend: List<DailyMoodBucket> = emptyList(),
        val metrics: List<MetricSnapshot> = emptyList(),
        val totalMoods: Int = 0,
        val totalJournals: Int = 0,
        val mirror: MirrorGenerator.Mirror? = null,
    )

    private val _mirror = MutableStateFlow<MirrorGenerator.Mirror?>(null)

    val state: StateFlow<UiState> = combine(
        mood.observeDailyAggregate(Time.daysAgoMillis(30), Long.MAX_VALUE),
        metric.observeLatestPerType(),
        mood.observeCount(),
        journal.observeCount(),
        _mirror,
    ) { trend, metrics, totalMoods, totalJournals, mirror ->
        UiState(trend, metrics, totalMoods, totalJournals, mirror)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        UiState(),
    )

    init {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val from = Time.daysAgoMillis(30)
            val label = "Your month \u2014 ${monthLabel(from)} to ${monthLabel(now)}"
            _mirror.value = mirrorGen.generate(from, now, label)
        }
    }

    private fun monthLabel(millis: Long): String =
        SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(millis))
}
