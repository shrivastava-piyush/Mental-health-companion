package com.wellness.companion.ui.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wellness.companion.data.db.entities.JournalEntry
import com.wellness.companion.data.repository.JournalRepository
import com.wellness.companion.domain.narrative.ColdOpenGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class JournalEditorViewModel(
    private val repo: JournalRepository,
    private val coldOpen: ColdOpenGenerator,
    private val entryId: Long,
) : ViewModel() {

    data class UiState(
        val id: Long = 0,
        val title: String = "",
        val body: String = "",
        val savedAt: Long? = null,
        val loaded: Boolean = false,
        val coldOpen: ColdOpenGenerator.ColdOpen? = null,
    )

    private val _state = MutableStateFlow(UiState())
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
            _state.value = UiState(loaded = true)
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
