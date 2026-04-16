package com.wellness.companion.ui.mood

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wellness.companion.data.db.DailyMoodBucket
import com.wellness.companion.data.db.MetricSnapshot
import com.wellness.companion.data.db.entities.MetricType
import com.wellness.companion.data.db.entities.MoodEntry
import com.wellness.companion.data.repository.MetricRepository
import com.wellness.companion.data.repository.MoodRepository
import com.wellness.companion.domain.Time
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * MVI-ish state holder for the Mood screen.
 *
 * We flatten all data streams into one [UiState] so the composable does not
 * subscribe to three separate flows (each triggering its own recomposition).
 */
class MoodViewModel(
    private val moodRepo: MoodRepository,
    private val metricRepo: MetricRepository,
) : ViewModel() {

    data class UiState(
        val recent: List<MoodEntry> = emptyList(),
        val trend: List<DailyMoodBucket> = emptyList(),
        val metrics: List<MetricSnapshot> = emptyList(),
        val draft: Draft = Draft(),
    )

    data class Draft(
        val valence: Int = 0,
        val arousal: Int = 2,
        val label: String = "balanced",
        val note: String = "",
    )

    sealed interface Intent {
        data class UpdateValence(val value: Int) : Intent
        data class UpdateArousal(val value: Int) : Intent
        data class UpdateLabel(val value: String) : Intent
        data class UpdateNote(val value: String) : Intent
        data object SaveMood : Intent
        data class LogMetric(val type: MetricType, val value: Double) : Intent
        data class DeleteMood(val id: Long) : Intent
    }

    // Drafts live as plain mutable state reduced through [send] to mirror MVI.
    private val _draft = MutableStateFlow(Draft())

    val state: StateFlow<UiState> = combine(
        moodRepo.observeRange(Time.daysAgoMillis(14), Long.MAX_VALUE),
        moodRepo.observeDailyAggregate(Time.daysAgoMillis(30), Long.MAX_VALUE),
        metricRepo.observeLatestPerType(),
        _draft,
    ) { recent, trend, metrics, draft ->
        UiState(recent, trend, metrics, draft)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        UiState(),
    )

    fun send(intent: Intent) {
        when (intent) {
            is Intent.UpdateValence -> _draft.value = _draft.value.copy(valence = intent.value)
            is Intent.UpdateArousal -> _draft.value = _draft.value.copy(arousal = intent.value)
            is Intent.UpdateLabel   -> _draft.value = _draft.value.copy(label   = intent.value)
            is Intent.UpdateNote    -> _draft.value = _draft.value.copy(note    = intent.value.take(280))
            Intent.SaveMood         -> save()
            is Intent.LogMetric     -> viewModelScope.launch { metricRepo.log(intent.type, intent.value) }
            is Intent.DeleteMood    -> viewModelScope.launch { moodRepo.delete(intent.id) }
        }
    }

    private fun save() {
        val d = _draft.value
        viewModelScope.launch {
            moodRepo.log(
                MoodEntry(
                    createdAt = System.currentTimeMillis(),
                    valence   = d.valence,
                    arousal   = d.arousal,
                    label     = d.label.ifBlank { "balanced" },
                    note      = d.note,
                )
            )
            _draft.value = Draft(valence = d.valence, arousal = d.arousal) // keep intensity, clear note
        }
    }
}
