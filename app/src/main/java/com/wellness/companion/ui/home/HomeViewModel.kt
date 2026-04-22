package com.wellness.companion.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wellness.companion.data.db.entities.MoodEntry
import com.wellness.companion.data.db.JournalSummary
import com.wellness.companion.data.repository.MoodRepository
import com.wellness.companion.data.repository.JournalRepository
import com.wellness.companion.domain.llm.ReflectionEngine
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

class HomeViewModel(
    private val moodRepo: MoodRepository,
    private val journalRepo: JournalRepository,
    private val reflectionEngine: ReflectionEngine?
) : ViewModel() {

    data class UiState(
        val recentMood: MoodEntry? = null,
        val recentJournal: JournalSummary? = null,
        val greeting: String = "Breathe.",
        val quote: Pair<String, String> = "Focus on the present moment." to "Breathe",
        val aiSpark: String? = null
    )

    private val quotes = listOf(
        "The only way to make sense out of change is to plunge into it, move with it, and join the dance." to "Alan Watts",
        "Knowing yourself is the beginning of all wisdom." to "Aristotle",
        "The wound is the place where the Light enters you." to "Rumi",
        "Your vision will become clear only when you can look into your own heart." to "Carl Jung"
    )

    private val _state = MutableStateFlow(UiState(quote = quotes.random()))
    val state: StateFlow<UiState> = _state.asStateFlow()

    init {
        updateGreeting()
        observeData()
    }

    private fun updateGreeting() {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greeting = when {
            hour < 12 -> "Morning, you."
            hour < 17 -> "Still your day."
            else -> "Rest, now."
        }
        _state.value = _state.value.copy(greeting = greeting)
    }

    private fun observeData() {
        viewModelScope.launch {
            combine(
                moodRepo.observeRange(0, Long.MAX_VALUE),
                journalRepo.observeSummaries()
            ) { moods, journalSummaries ->
                moods.lastOrNull() to journalSummaries.firstOrNull()
            }.collect { (mood, journal) ->
                _state.update { it.copy(recentMood = mood, recentJournal = journal) }
                // If we have an AI engine, try to get a spark
                if (reflectionEngine != null) {
                    val tod = getTod()
                    val spark = reflectionEngine.contextualStarter(mood?.label, tod)
                    _state.update { it.copy(aiSpark = spark) }
                }
            }
        }
    }

    private fun getTod(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when {
            hour < 6 -> "late night"
            hour < 12 -> "morning"
            hour < 17 -> "afternoon"
            hour < 21 -> "evening"
            else -> "night"
        }
    }
}
