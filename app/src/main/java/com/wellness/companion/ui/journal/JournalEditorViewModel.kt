package com.wellness.companion.ui.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wellness.companion.data.db.MoodDao
import com.wellness.companion.data.db.entities.JournalEntry
import com.wellness.companion.data.repository.JournalRepository
import com.wellness.companion.domain.llm.ReflectionEngine
import com.wellness.companion.domain.narrative.ColdOpenGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    data class GuidedExchange(val question: String, val answer: String)

    data class UiState(
        val id: Long = 0,
        val title: String = "",
        val body: String = "",
        val savedAt: Long? = null,
        val loaded: Boolean = false,
        val coldOpen: ColdOpenGenerator.ColdOpen? = null,
        val reflectionQuestions: List<String> = emptyList(),
        val reframeText: String = "",
        val reflecting: Boolean = false,
        val reframing: Boolean = false,
        val hasLlm: Boolean = false,
        val starterPrompt: String = "",
        val goDeeperNudge: String = "",
        val nudging: Boolean = false,
        val titleSuggestion: String = "",
        val suggestingTitle: Boolean = false,
        val guidedMode: Boolean = false,
        val guidedExchanges: List<GuidedExchange> = emptyList(),
        val guidedCurrentQuestion: String = "",
        val guidedAnswer: String = "",
        val guidedGenerating: Boolean = false,
        val guidedComplete: Boolean = false,
    )

    private val _state = MutableStateFlow(UiState(hasLlm = reflection != null))
    val state: StateFlow<UiState> = _state.asStateFlow()

    init {
        if (entryId > 0L) {
            viewModelScope.launch {
                repo.observeById(entryId).collect { entry ->
                    if (entry != null) {
                        _state.value = _state.value.copy(
                            id = entry.id,
                            title = entry.title,
                            body = entry.body,
                            savedAt = entry.updatedAt,
                            loaded = true,
                        )
                    }
                }
            }
        } else {
            _state.value = _state.value.copy(
                loaded = true,
                starterPrompt = prefilledPrompt,
            )
            viewModelScope.launch {
                val prompt = coldOpen.generate()
                _state.value = _state.value.copy(coldOpen = prompt)
            }
            if (prefilledPrompt.isBlank()) generateStarterPrompt()
        }
    }

    fun onTitleChange(value: String) { _state.value = _state.value.copy(title = value.take(120)) }
    fun onBodyChange(value: String)  { _state.value = _state.value.copy(body = value) }
    fun dismissColdOpen()            { _state.value = _state.value.copy(coldOpen = null) }

    fun save(onSaved: (Long) -> Unit) {
        val s = _state.value
        if (s.title.isBlank() && s.body.isBlank()) return
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val savedId = repo.save(
                JournalEntry(
                    id = s.id,
                    createdAt = if (s.id == 0L) now else s.savedAt ?: now,
                    updatedAt = now,
                    title = s.title.ifBlank { "Untitled" },
                    body = s.body,
                    wordCount = s.body.split(WordSplit).count { it.isNotBlank() },
                )
            )
            _state.value = s.copy(id = savedId, savedAt = now)
            onSaved(savedId)
            triggerReflection(s.title.ifBlank { "Untitled" }, s.body)
        }
    }

    fun requestReframe() {
        val s = _state.value
        if (s.body.isBlank() || s.reframing) return
        viewModelScope.launch {
            _state.value = _state.value.copy(reframing = true)
            val result = reflection?.reframe(s.title.ifBlank { "Untitled" }, s.body)
            _state.value = _state.value.copy(
                reframeText = result?.text ?: "",
                reframing = false,
            )
        }
    }

    fun dismissReflection() { _state.value = _state.value.copy(reflectionQuestions = emptyList()) }
    fun dismissReframe()    { _state.value = _state.value.copy(reframeText = "") }
    fun dismissNudge()      { _state.value = _state.value.copy(goDeeperNudge = "") }

    fun requestGoDeeper() {
        val s = _state.value
        if (s.body.isBlank() || s.nudging || reflection == null) return
        viewModelScope.launch {
            _state.value = _state.value.copy(nudging = true)
            val nudge = reflection.goDeeper(s.body)
            _state.value = _state.value.copy(
                goDeeperNudge = nudge ?: "",
                nudging = false,
            )
        }
    }

    fun requestTitleSuggestion() {
        val s = _state.value
        if (s.body.isBlank() || s.suggestingTitle || reflection == null) return
        viewModelScope.launch {
            _state.value = _state.value.copy(suggestingTitle = true)
            val title = reflection.suggestTitle(s.body)
            _state.value = _state.value.copy(
                titleSuggestion = title ?: "",
                suggestingTitle = false,
            )
        }
    }

    fun acceptTitleSuggestion() {
        val suggestion = _state.value.titleSuggestion
        if (suggestion.isNotBlank()) {
            _state.value = _state.value.copy(title = suggestion, titleSuggestion = "")
        }
    }

    fun dismissTitleSuggestion() { _state.value = _state.value.copy(titleSuggestion = "") }

    // ── Guided mode ──────────────────────────────────────────────

    fun startGuidedMode() {
        if (reflection == null) return
        _state.value = _state.value.copy(guidedMode = true, guidedGenerating = true)
        viewModelScope.launch {
            val question = reflection.guidedQuestion(emptyList())
            _state.value = _state.value.copy(
                guidedCurrentQuestion = question ?: "What's on your mind right now?",
                guidedGenerating = false,
            )
        }
    }

    fun onGuidedAnswerChange(value: String) {
        _state.value = _state.value.copy(guidedAnswer = value)
    }

    fun submitGuidedAnswer() {
        val s = _state.value
        if (s.guidedAnswer.isBlank() || s.guidedGenerating) return
        val newExchange = GuidedExchange(s.guidedCurrentQuestion, s.guidedAnswer)
        val exchanges = s.guidedExchanges + newExchange

        if (exchanges.size >= MAX_GUIDED_EXCHANGES) {
            _state.value = s.copy(
                guidedExchanges = exchanges,
                guidedAnswer = "",
                guidedGenerating = true,
                guidedComplete = true,
            )
            compileGuidedEntry(exchanges)
        } else {
            _state.value = s.copy(
                guidedExchanges = exchanges,
                guidedAnswer = "",
                guidedGenerating = true,
            )
            viewModelScope.launch {
                val pairs = exchanges.map { it.question to it.answer }
                val next = reflection?.guidedQuestion(pairs)
                _state.value = _state.value.copy(
                    guidedCurrentQuestion = next ?: "What else comes to mind?",
                    guidedGenerating = false,
                )
            }
        }
    }

    fun finishGuidedEarly() {
        val exchanges = _state.value.guidedExchanges
        if (exchanges.isEmpty()) {
            _state.value = _state.value.copy(guidedMode = false)
            return
        }
        _state.value = _state.value.copy(guidedGenerating = true, guidedComplete = true)
        compileGuidedEntry(exchanges)
    }

    fun exitGuidedMode() {
        _state.value = _state.value.copy(guidedMode = false)
    }

    private fun compileGuidedEntry(exchanges: List<GuidedExchange>) {
        viewModelScope.launch {
            val pairs = exchanges.map { it.question to it.answer }
            val compiled = reflection?.compileGuided(pairs)
            val body = compiled ?: exchanges.joinToString("\n\n") { it.answer }
            _state.value = _state.value.copy(
                body = body,
                guidedMode = false,
                guidedGenerating = false,
            )
            val title = reflection?.suggestTitle(body)
            if (!title.isNullOrBlank()) {
                _state.value = _state.value.copy(title = title)
            }
        }
    }

    // ── Internal ─────────────────────────────────────────────────

    private fun generateStarterPrompt() {
        if (reflection == null) return
        viewModelScope.launch {
            val labels = moodDao.recentLabels(1)
            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            val timeOfDay = when {
                hour < 6 -> "late night"
                hour < 12 -> "morning"
                hour < 17 -> "afternoon"
                hour < 21 -> "evening"
                else -> "night"
            }
            val starter = reflection.contextualStarter(labels.firstOrNull(), timeOfDay)
            if (!starter.isNullOrBlank()) {
                _state.value = _state.value.copy(starterPrompt = starter)
            }
        }
    }

    private fun triggerReflection(title: String, body: String) {
        if (reflection == null) return
        viewModelScope.launch {
            _state.value = _state.value.copy(reflecting = true)
            val result = reflection.reflect(title, body)
            _state.value = _state.value.copy(
                reflectionQuestions = result?.questions ?: emptyList(),
                reflecting = false,
            )
        }
    }

    fun delete(onDeleted: () -> Unit) {
        val id = _state.value.id
        if (id == 0L) { onDeleted(); return }
        viewModelScope.launch { repo.delete(id); onDeleted() }
    }

    private companion object {
        val WordSplit = Regex("\\s+")
        const val MAX_GUIDED_EXCHANGES = 5
    }
}
