package com.wellness.companion.ui.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wellness.companion.data.db.entities.JournalEntry
import com.wellness.companion.data.repository.JournalRepository
import com.wellness.companion.domain.llm.ReflectionEngine
import com.wellness.companion.domain.narrative.ColdOpenGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class JournalEditorViewModel(
    private val repo: JournalRepository,
    private val coldOpen: ColdOpenGenerator,
    private val reflection: ReflectionEngine?,
    private val entryId: Long,
) : ViewModel() {

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
            _state.value = _state.value.copy(loaded = true)
            viewModelScope.launch {
                val prompt = coldOpen.generate()
                _state.value = _state.value.copy(coldOpen = prompt)
            }
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

    fun dismissReflection() {
        _state.value = _state.value.copy(reflectionQuestions = emptyList())
    }

    fun dismissReframe() {
        _state.value = _state.value.copy(reframeText = "")
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
    }
}
