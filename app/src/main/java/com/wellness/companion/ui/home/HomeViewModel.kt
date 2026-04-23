package com.wellness.companion.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wellness.companion.data.db.entities.MoodEntry
import com.wellness.companion.data.db.JournalSummary
import com.wellness.companion.data.repository.MoodRepository
import com.wellness.companion.data.repository.JournalRepository
import com.wellness.companion.domain.llm.ReflectionEngine
import com.wellness.companion.ui.MoodCategory
import com.wellness.companion.ui.WellnessContentProvider
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
        val quote: Pair<String, String> = "Focus on the present moment." to "Zen Proverb",
        val aiSpark: String? = null
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            combine(
                moodRepo.observeRange(0, Long.MAX_VALUE),
                journalRepo.observeSummaries()
            ) { moods, journalSummaries ->
                moods.lastOrNull() to journalSummaries.firstOrNull()
            }.collect { (mood, journal) ->
                val valence = mood?.valence ?: 0
                val category = MoodCategory.fromValence(valence)
                val quote = WellnessContentProvider.getQuote(category)

                _state.update { it.copy(
                    recentMood = mood, 
                    recentJournal = journal,
                    quote = quote.text to quote.author
                ) }

                if (reflectionEngine != null) {
                    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                    val tod = when {
                        hour < 6 -> "late night"
                        hour < 12 -> "morning"
                        hour < 17 -> "afternoon"
                        hour < 21 -> "evening"
                        else -> "night"
                    }
                    val spark = reflectionEngine.contextualStarter(mood?.label, tod)
                    _state.update { it.copy(aiSpark = spark) }
                }
            }
        }
    }
}
