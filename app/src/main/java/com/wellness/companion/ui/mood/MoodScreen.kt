package com.wellness.companion.ui.mood

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wellness.companion.data.db.entities.MoodEntry
import com.wellness.companion.di.AppContainer
import com.wellness.companion.di.ViewModelFactories
import com.wellness.companion.ui.components.MoodTrendChart
import com.wellness.companion.ui.components.WellnessWheel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val MoodLabels = listOf("grateful", "calm", "balanced", "anxious", "tired", "energised", "sad", "hopeful")

@Composable
fun MoodScreen(container: AppContainer, contentPadding: PaddingValues) {
    val vm: MoodViewModel = viewModel(factory = ViewModelFactories.mood(container))
    val state by vm.state.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { vm.send(MoodViewModel.Intent.SaveMood) },
                icon = { Icon(Icons.Filled.Check, contentDescription = null) },
                text = { Text("Save mood") },
                containerColor = MaterialTheme.colorScheme.primary,
            )
        },
    ) { inner ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = contentPadding.calculateTopPadding() + 8.dp,
                bottom = contentPadding.calculateBottomPadding() + 96.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            item {
                Text(
                    "How are you, right now?",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    "Drag the dot to capture valence and energy.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            item {
                WellnessWheel(
                    valence = state.draft.valence,
                    arousal = state.draft.arousal,
                    onChange = { v, a ->
                        vm.send(MoodViewModel.Intent.UpdateValence(v))
                        vm.send(MoodViewModel.Intent.UpdateArousal(a))
                    },
                )
            }

            item { LabelRow(selected = state.draft.label, onSelect = { vm.send(MoodViewModel.Intent.UpdateLabel(it)) }) }

            item {
                OutlinedTextField(
                    value = state.draft.note,
                    onValueChange = { vm.send(MoodViewModel.Intent.UpdateNote(it)) },
                    label = { Text("Add a note (optional)") },
                    placeholder = { Text("What's happening?") },
                    singleLine = false,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                )
            }

            item {
                Text("Last 30 days", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(6.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(22.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    MoodTrendChart(
                        buckets = state.trend,
                        modifier = Modifier.padding(12.dp),
                    )
                }
            }

            item {
                Text("Recent entries", style = MaterialTheme.typography.titleMedium)
            }

            items(state.recent, key = { it.id }) { entry ->
                MoodRow(entry)
            }

            if (state.recent.isEmpty()) {
                item {
                    Text(
                        "Your first log will appear here.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun LabelRow(selected: String, onSelect: (String) -> Unit) {
    androidx.compose.foundation.lazy.LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(MoodLabels) { label ->
            FilterChip(
                selected = selected == label,
                onClick = { onSelect(label) },
                label = { Text(label) },
            )
        }
    }
}

@Composable
private fun MoodRow(entry: MoodEntry) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(entry.label.replaceFirstChar { it.titlecase(Locale.getDefault()) },
                    style = MaterialTheme.typography.titleMedium)
                if (entry.note.isNotBlank()) {
                    Text(
                        entry.note,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Text(
                dateFormatter.format(Date(entry.createdAt)),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private val dateFormatter = SimpleDateFormat("EEE, HH:mm", Locale.getDefault())
