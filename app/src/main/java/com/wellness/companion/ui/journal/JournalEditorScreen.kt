package com.wellness.companion.ui.journal

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wellness.companion.di.AppContainer
import com.wellness.companion.di.ViewModelFactories
import com.wellness.companion.ui.components.LiquidAura
import com.wellness.companion.ui.theme.WellnessPalette

@Composable
fun JournalEditorScreen(
    container: AppContainer,
    entryId: Long = 0L,
    prefilledPrompt: String = "",
    onBack: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val viewModel: JournalEditorViewModel = viewModel(
        key = "editor-$entryId-${prefilledPrompt.hashCode()}",
        factory = ViewModelFactories.journalEditor(container, entryId, prefilledPrompt),
    )
    val state by viewModel.state.collectAsState()
    
    var guidedMode by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        LiquidAura()

        if (guidedMode) {
            GuidedFlow(
                viewModel = viewModel,
                onComplete = { guidedMode = false },
                onCancel = { guidedMode = false }
            )
        } else {
            EditorContent(
                state = state,
                viewModel = viewModel,
                onBack = onBack,
                onStartGuided = { guidedMode = true }
            )
        }
    }
}

@Composable
private fun EditorContent(
    state: JournalEditorViewModel.UiState,
    viewModel: JournalEditorViewModel,
    onBack: () -> Unit,
    onStartGuided: () -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = { viewModel.send(JournalEditorViewModel.Intent.Save); onBack() }) {
                Text("Done", fontWeight = FontWeight.Bold, color = Color.White)
            }
            Text("REFLECTION", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, color = Color.White.copy(alpha = 0.4f), letterSpacing = 2.sp)
            if (state.id > 0) {
                IconButton(onClick = { viewModel.send(JournalEditorViewModel.Intent.Delete); onBack() }) {
                    Icon(Icons.Default.Delete, null, tint = Color.White.copy(alpha = 0.4f))
                }
            } else {
                Spacer(Modifier.width(48.dp))
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp)
        ) {
            if (state.id <= 0 && state.body.isEmpty()) {
                GuidedNudge(onStartGuided)
                Spacer(Modifier.height(32.dp))
            }

            TextField(
                value = state.title,
                onValueChange = { viewModel.send(JournalEditorViewModel.Intent.UpdateTitle(it)) },
                placeholder = { Text("UNTITLED", color = Color.White.copy(alpha = 0.2f)) },
                textStyle = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black, fontFamily = FontFamily.SansSerif),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            BasicTextField(
                value = state.body,
                onValueChange = { viewModel.send(JournalEditorViewModel.Intent.UpdateBody(it)) },
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White,
                    fontFamily = FontFamily.Serif,
                    lineHeight = 32.sp,
                    fontSize = 20.sp
                ),
                cursorBrush = SolidColor(Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 500.dp),
                decorationBox = { innerTextField ->
                    if (state.body.isEmpty()) {
                        Text("Speak your truth…", color = Color.White.copy(alpha = 0.2f), style = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.Serif, fontSize = 20.sp))
                    }
                    innerTextField()
                }
            )

            Spacer(Modifier.height(150.dp))
        }
    }
}

@Composable
private fun GuidedNudge(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = Color.White.copy(alpha = 0.08f),
        shape = RoundedCornerShape(32.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Icon(Icons.Default.AutoAwesome, null, tint = Color.Cyan)
            Column(Modifier.weight(1f)) {
                Text("Deep Reflection", fontWeight = FontWeight.Bold, color = Color.White)
                Text("Let AI guide your exploration", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.5f))
            }
            Icon(Icons.Default.ArrowCircleRight, null, tint = Color.White.copy(alpha = 0.2f))
        }
    }
}

@Composable
private fun GuidedFlow(
    viewModel: JournalEditorViewModel,
    onComplete: () -> Unit,
    onCancel: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()
    
    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().statusBarsPadding().padding(28.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onCancel) { Text("Cancel", color = Color.White.copy(alpha = 0.6f)) }
            Text("GUIDED", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, color = Color.White.copy(alpha = 0.4f), letterSpacing = 2.sp)
            if (state.guidedExchanges.isNotEmpty()) {
                TextButton(onClick = { viewModel.send(JournalEditorViewModel.Intent.FinishGuided); onComplete() }) {
                    Text("Finish", fontWeight = FontWeight.Bold, color = Color.Cyan)
                }
            } else {
                Spacer(Modifier.width(50.dp))
            }
        }

        Column(Modifier.weight(1f).verticalScroll(scrollState).padding(horizontal = 28.dp), verticalArrangement = Arrangement.spacedBy(32.dp)) {
            state.guidedExchanges.forEach { (q, a) ->
                GuidedBubble(q, isUser = false)
                GuidedBubble(a, isUser = true)
            }
            if (state.guidedCurrentQuestion.isNotBlank()) {
                GuidedBubble(state.guidedCurrentQuestion, isUser = false)
            }
            if (state.isGenerating) {
                CircularProgressIndicator(Modifier.size(24.dp).padding(4.dp), color = Color.White, strokeWidth = 2.dp)
            }
            Spacer(Modifier.height(40.dp))
        }

        if (state.guidedCurrentQuestion.isNotBlank() && !state.isGenerating) {
            LiquidInputArea(
                value = state.guidedAnswer,
                onValueChange = { viewModel.send(JournalEditorViewModel.Intent.UpdateGuidedAnswer(it)) },
                onSend = { viewModel.send(JournalEditorViewModel.Intent.SubmitGuidedAnswer) }
            )
        }
    }
    
    LaunchedEffect(state.guidedExchanges.size, state.guidedCurrentQuestion) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }
}

@Composable
private fun GuidedBubble(text: String, isUser: Boolean) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start) {
        if (isUser) Spacer(Modifier.width(60.dp))
        Text(
            text = text,
            modifier = Modifier
                .clip(RoundedCornerShape(28.dp))
                .background(if (isUser) WellnessPalette.Sage500.copy(alpha = 0.6f) else Color.Cyan.copy(alpha = 0.1f))
                .padding(20.dp),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = if (isUser) FontFamily.Default else FontFamily.Serif,
                fontWeight = if (isUser) FontWeight.Bold else FontWeight.Medium
            ),
            color = Color.White
        )
        if (!isUser) Spacer(Modifier.width(60.dp))
    }
}

@Composable
private fun LiquidInputArea(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Surface(
        color = Color.White.copy(alpha = 0.05f),
        modifier = Modifier.fillMaxWidth().navigationBarsPadding()
    ) {
        Row(Modifier.padding(24.dp), verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type here…", color = Color.White.copy(alpha = 0.3f)) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White.copy(alpha = 0.1f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(24.dp)
            )
            IconButton(
                onClick = onSend,
                enabled = value.isNotBlank(),
                modifier = Modifier.size(54.dp).clip(CircleShape).background(if (value.isNotBlank()) Color.White else Color.White.copy(alpha = 0.2f))
            ) {
                Icon(Icons.Default.ArrowUpward, null, tint = WellnessPalette.LiquidDeep)
            }
        }
    }
}
