package com.wellness.companion.ui.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wellness.companion.data.db.DailyMoodBucket
import com.wellness.companion.data.db.MetricSnapshot
import com.wellness.companion.data.repository.JournalRepository
import com.wellness.companion.data.repository.MetricRepository
import com.wellness.companion.data.repository.MoodRepository
import com.wellness.companion.domain.Time
import com.wellness.companion.domain.llm.ReflectionEngine
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
    private val reflection: ReflectionEngine?,
) : ViewModel() {

    data class UiState(
        val trend: List<DailyMoodBucket> = emptyList(),
        val metrics: List<MetricSnapshot> = emptyList(),
        val totalMoods: Int = 0,
        val totalJournals: Int = 0,
        val mirror: MirrorGenerator.Mirror? = null,
        val patternNarrative: String = "",
    )

    private val _mirror = MutableStateFlow<MirrorGenerator.Mirror?>(null)
    private val _narrative = MutableStateFlow("")

    val state: StateFlow<UiState> = combine(
        mood.observeDailyAggregate(Time.daysAgoMillis(30), Long.MAX_VALUE),
        metric.observeLatestPerType(),
        mood.observeCount(),
        journal.observeCount(),
        _mirror,
        _narrative,
    ) { values ->
        @Suppress("UNCHECKED_CAST")
        UiState(
            trend = values[0] as List<DailyMoodBucket>,
            metrics = values[1] as List<MetricSnapshot>,
            totalMoods = values[2] as Int,
            totalJournals = values[3] as Int,
            mirror = values[4] as MirrorGenerator.Mirror?,
            patternNarrative = values[5] as String,
        )
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
            val mirror = mirrorGen.generate(from, now, label)
            _mirror.value = mirror

            if (mirror != null && reflection != null) {
                val narrative = reflection.narrateMirror(mirror)
                _narrative.value = narrative ?: ""
            }
        }
    }

    fun downloadModel(modelManager: com.wellness.companion.data.llm.ModelManager) {
        viewModelScope.launch {
            modelManager.download(MODEL_URL)
        }
    }

    private fun monthLabel(millis: Long): String =
        SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(millis))

    private companion object {
        const val MODEL_URL = "https://huggingface.co/TheBloke/TinyLlama-1.1B-Chat-v1.0-GGUF/resolve/main/tinyllama-1.1b-chat-v1.0.Q4_K_M.gguf"
    }
}
