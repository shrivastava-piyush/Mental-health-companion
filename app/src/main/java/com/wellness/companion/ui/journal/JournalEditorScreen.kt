package com.wellness.companion.ui.journal

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wellness.companion.di.AppContainer
import com.wellness.companion.di.ViewModelFactories
import com.wellness.companion.domain.narrative.ColdOpenGenerator
import com.wellness.companion.ui.components.ReflectionCard
import com.wellness.companion.ui.components.ReframeCard

@Composable
fun JournalEditorScreen(
    container: AppContainer,
    entryId: Long,
    onBack: () -> Unit,
    contentPadding: PaddingValues,
    prefilledPrompt: String = "",
    onOpenJournalWithPrompt: (String) -> Unit = {},
) {
    val vm: JournalEditorViewModel = viewModel(
        key = "editor-$entryId-${prefilledPrompt.hashCode()}",
        factory = ViewModelFactories.journalEditor(container, entryId, prefilledPrompt),
    )
    val state by vm.state.collectAsStateWithLifecycle()

    if (state.guidedMode) {
        GuidedJournalScreen(vm, state, onBack, contentPadding)
        return
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(if (state.id == 0L) "New entry" else "Edit entry") },
                navigationIcon = {
                    IconButton(onClick = { vm.save { onBack() } }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Save and back")
                    }
                },
                actions = {
                    IconButton(onClick = { vm.save { /* stay */ } }) {
                        Icon(Icons.Outlined.Save, contentDescription = "Save")
                    }
                    if (state.id > 0L) {
                        IconButton(onClick = { vm.delete { onBack() } }) {
                            Icon(Icons.Outlined.Delete, contentDescription = "Delete")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = contentPadding.calculateBottomPadding() + 24.dp),
        ) {
            AnimatedVisibility(
                visible = state.coldOpen != null,
                enter = fadeIn() + slideInVertically { -it / 3 },
                exit = fadeOut(),
            ) {
                state.coldOpen?.let { co ->
                    ColdOpenCard(
                        coldOpen = co,
                        onDismiss = { vm.dismissColdOpen() },
                        modifier = Modifier.padding(bottom = 16.dp),
                    )
                }
            }

            if (state.hasLlm && state.id == 0L && state.body.isEmpty()) {
                AssistChip(
                    onClick = { vm.startGuidedMode() },
                    label = { Text("Guide me") },
                    leadingIcon = { Icon(Icons.Outlined.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.padding(bottom = 12.dp),
                )
            }

            BasicTextField(
                value = state.title,
                onValueChange = vm::onTitleChange,
                textStyle = MaterialTheme.typography.headlineMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                ),
                singleLine = true,
                decorationBox = { innerField ->
                    if (state.title.isEmpty()) {
                        Text(
                            "Title",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    innerField()
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            )

            val placeholder = state.starterPrompt.ifBlank { "Write freely\u2026" }
            BasicTextField(
                value = state.body,
                onValueChange = vm::onBodyChange,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                ),
                decorationBox = { innerField ->
                    if (state.body.isEmpty()) {
                        Text(
                            placeholder,
                            style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    innerField()
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            )

            if (state.hasLlm && state.body.split(Regex("\\s+")).size >= 15) {
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (state.goDeeperNudge.isBlank() && !state.nudging) {
                        TextButton(onClick = { vm.requestGoDeeper() }) {
                            Text("Go deeper")
                        }
                    }
                    if (state.title.isBlank() && state.titleSuggestion.isBlank() && !state.suggestingTitle) {
                        TextButton(onClick = { vm.requestTitleSuggestion() }) {
                            Text("Suggest title")
                        }
                    }
                    if (state.nudging || state.suggestingTitle) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    }
                }
            }

            AnimatedVisibility(
                visible = state.goDeeperNudge.isNotBlank(),
                enter = fadeIn() + slideInVertically { it / 3 },
            ) {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                        Text(
                            state.goDeeperNudge,
                            style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.weight(1f),
                        )
                        IconButton(onClick = { vm.dismissNudge() }) {
                            Icon(Icons.Outlined.Close, contentDescription = "Dismiss",
                                tint = MaterialTheme.colorScheme.onSecondaryContainer)
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = state.titleSuggestion.isNotBlank(),
                enter = fadeIn() + slideInVertically { it / 3 },
            ) {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            "\u201C${state.titleSuggestion}\u201D",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextButton(onClick = { vm.acceptTitleSuggestion() }) { Text("Use this") }
                            TextButton(onClick = { vm.dismissTitleSuggestion() }) { Text("No thanks") }
                        }
                    }
                }
            }

            if (state.hasLlm && state.savedAt != null && state.body.isNotBlank()) {
                Spacer(Modifier.height(24.dp))
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (state.reflecting) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    }

                    ReflectionCard(
                        questions = state.reflectionQuestions,
                        visible = state.reflectionQuestions.isNotEmpty(),
                        onQuestionClick = onOpenJournalWithPrompt.takeIf { state.reflectionQuestions.isNotEmpty() },
                    )

                    if (state.reframeText.isBlank() && !state.reframing && state.reflectionQuestions.isNotEmpty()) {
                        TextButton(onClick = { vm.requestReframe() }) {
                            Text("See it from another angle")
                        }
                    }
                    if (state.reframing) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    }
                    ReframeCard(
                        text = state.reframeText,
                        visible = state.reframeText.isNotBlank(),
                    )
                }
            }
        }
    }
}

@Composable
private fun GuidedJournalScreen(
    vm: JournalEditorViewModel,
    state: JournalEditorViewModel.UiState,
    onBack: () -> Unit,
    contentPadding: PaddingValues,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Guided entry") },
                navigationIcon = {
                    IconButton(onClick = { vm.exitGuidedMode(); onBack() }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .padding(
                    top = 8.dp,
                    bottom = contentPadding.calculateBottomPadding() + 24.dp,
                ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                "Answer what feels true. Short is fine.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            state.guidedExchanges.forEach { exchange ->
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            exchange.question,
                            style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            exchange.answer,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }

            if (state.guidedGenerating) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(12.dp))
                    Text(
                        if (state.guidedComplete) "Weaving your entry\u2026" else "Thinking\u2026",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            if (!state.guidedComplete && !state.guidedGenerating && state.guidedCurrentQuestion.isNotBlank()) {
                Text(
                    state.guidedCurrentQuestion,
                    style = MaterialTheme.typography.titleMedium.copy(fontStyle = FontStyle.Italic),
                    color = MaterialTheme.colorScheme.onBackground,
                )

                OutlinedTextField(
                    value = state.guidedAnswer,
                    onValueChange = vm::onGuidedAnswerChange,
                    placeholder = { Text("Your answer\u2026") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { vm.submitGuidedAnswer() },
                        enabled = state.guidedAnswer.isNotBlank(),
                    ) {
                        Text("Next")
                    }
                    if (state.guidedExchanges.isNotEmpty()) {
                        OutlinedButton(onClick = { vm.finishGuidedEarly() }) {
                            Text("That's enough")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ColdOpenCard(
    coldOpen: ColdOpenGenerator.ColdOpen,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        ),
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(16.dp)) {
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.padding(start = 0.dp),
            ) {
                Icon(
                    Icons.Outlined.Close,
                    contentDescription = "Dismiss",
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                )
            }
            Text(
                coldOpen.reason,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "\u201C${coldOpen.snippet}\u201D",
                style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                color = MaterialTheme.colorScheme.onTertiaryContainer,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "\u2014 ${coldOpen.title}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f),
            )
        }
    }
}
