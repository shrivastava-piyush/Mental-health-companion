package com.wellness.companion.ui.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wellness.companion.data.db.entities.JournalEntry
import com.wellness.companion.data.repository.JournalRepository
import com.wellness.companion.domain.llm.ReflectionEngine
import com.wellness.companion.domain.narrative.ColdOpenGenerator
import com.wellness.companion.data.db.MoodDao
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

class JournalEditorViewModel(
    private val repo: JournalRepository,
    private val coldOpen: ColdOpenGenerator,
    private val reflection: ReflectionEngine?,
    private val moodDao: MoodDao,
    private val entryId: Long,
    private val prefilledPrompt: String = "",
) : ViewModel() {

    data class UiState(
        val id: Long = 0,
        val title: String = "",
        val body: String = "",
        val createdAt: Long = 0,
        val loaded: Boolean = false,
        val isGenerating: Boolean = false,
        val guidedCurrentQuestion: String = "",
        val guidedAnswer: String = "",
        val guidedExchanges: List<Pair<String, String>> = emptyList(),
        val starterPrompt: String = ""
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    init {
        load()
    }

    private fun load() {
        if (entryId > 0L) {
            viewModelScope.launch {
                repo.observeById(entryId).collect { entry ->
                    if (entry != null) {
                        _state.update { it.copy(
                            id = entry.id,
                            title = entry.title,
                            body = entry.body,
                            createdAt = entry.createdAt,
                            loaded = true
                        ) }
                    }
                }
            }
        } else {
            _state.update { it.copy(
                loaded = true,
                createdAt = System.currentTimeMillis(),
                starterPrompt = prefilledPrompt,
            ) }
        }
    }

    sealed interface Intent {
        data class UpdateTitle(val value: String) : Intent
        data class UpdateBody(val value: String) : Intent
        data object Save : Intent
        data object Delete : Intent
        data class UpdateGuidedAnswer(val value: String) : Intent
        data object SubmitGuidedAnswer : Intent
        data object FinishGuided : Intent
    }

    fun send(intent: Intent) {
        when (intent) {
            is Intent.UpdateTitle -> _state.update { it.copy(title = intent.value) }
            is Intent.UpdateBody -> _state.update { it.copy(body = intent.value) }
            Intent.Save -> save()
            Intent.Delete -> viewModelScope.launch { repo.delete(_state.value.id) }
            is Intent.UpdateGuidedAnswer -> _state.update { it.copy(guidedAnswer = intent.value) }
            Intent.SubmitGuidedAnswer -> submitGuidedAnswer()
            Intent.FinishGuided -> compileGuided()
        }
    }

    private fun save() {
        val s = _state.value
        if (s.title.isBlank() && s.body.isBlank()) return
        viewModelScope.launch {
            repo.save(
                JournalEntry(
                    id = s.id,
                    createdAt = s.createdAt,
                    updatedAt = System.currentTimeMillis(),
                    title = s.title.ifBlank { "Reflection" },
                    body = s.body,
                    wordCount = s.body.split(Regex("\\s+")).count { it.isNotBlank() }
                )
            )
        }
    }

    private fun submitGuidedAnswer() {
        val s = _state.value
        if (s.guidedAnswer.isBlank() || s.isGenerating) return
        val exchanges = s.guidedExchanges + (s.guidedCurrentQuestion to s.guidedAnswer)
        _state.update { it.copy(guidedExchanges = exchanges, guidedAnswer = "", isGenerating = true) }
        
        if (exchanges.size >= 4) {
            compileGuided()
        } else {
            viewModelScope.launch {
                val next = reflection?.guidedQuestion(exchanges) ?: "Tell me more."
                _state.update { it.copy(guidedCurrentQuestion = next, isGenerating = false) }
            }
        }
    }

    private fun compileGuided() {
        val s = _state.value
        _state.update { it.copy(isGenerating = true) }
        viewModelScope.launch {
            val compiled = reflection?.compileGuided(s.guidedExchanges) 
                ?: s.guidedExchanges.joinToString("\n\n") { it.second }
            val title = reflection?.suggestTitle(compiled)
            _state.update { it.copy(
                body = compiled,
                title = title ?: it.title,
                isGenerating = false,
                guidedCurrentQuestion = ""
            ) }
        }
    }
}
