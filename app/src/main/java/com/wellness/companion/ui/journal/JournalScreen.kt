package com.wellness.companion.ui.journal

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wellness.companion.data.db.JournalSummary
import com.wellness.companion.data.db.entities.NarrativeThread
import com.wellness.companion.di.AppContainer
import com.wellness.companion.di.ViewModelFactories
import com.wellness.companion.ui.components.LiquidAura
import com.wellness.companion.ui.theme.WellnessPalette
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun JournalListScreen(
    container: AppContainer,
    onOpen: (Long?) -> Unit,
    onOpenThread: (Long, String) -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val viewModel: JournalListViewModel = viewModel(factory = ViewModelFactories.journalList(container))
    val state by viewModel.state.collectAsState()

    Box(Modifier.fillMaxSize()) {
        LiquidAura()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(contentPadding),
            contentPadding = PaddingValues(horizontal = 28.dp, vertical = 20.dp)
        ) {
            item {
                Column(horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("REFLECTIONS", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, letterSpacing = 2.sp, color = Color.White.copy(alpha = 0.4f))
                    Text("Library", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("${state.entries.size} notes captured in sanctuary", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.5f))
                }
                Spacer(Modifier.height(40.dp))
            }

            if (state.threads.isNotEmpty()) {
                item {
                    Text("THEMES", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, letterSpacing = 2.sp, color = Color.White.copy(alpha = 0.4f))
                    Spacer(Modifier.height(20.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(state.threads) { thread ->
                            ThreadBubble(thread) { onOpenThread(thread.id, thread.label) }
                        }
                    }
                    Spacer(Modifier.height(40.dp))
                }
            }

            item {
                Text("TIMELINE", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, letterSpacing = 2.sp, color = Color.White.copy(alpha = 0.4f))
                Spacer(Modifier.height(24.dp))
            }

            if (state.entries.isEmpty()) {
                item {
                    Column(
                        Modifier.fillMaxWidth().padding(top = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(Icons.Default.AutoStories, null, Modifier.size(48.dp), tint = Color.White.copy(alpha = 0.1f))
                        Text("Your story begins here.", style = MaterialTheme.typography.titleMedium, color = Color.White.copy(alpha = 0.3f), fontWeight = FontWeight.Medium)
                    }
                }
            } else {
                items(state.entries) { summary ->
                    LiquidEntryCard(summary) { onOpen(summary.id) }
                    Spacer(Modifier.height(20.dp))
                }
            }

            item { Spacer(Modifier.height(150.dp)) }
        }

        // FAB
        Box(Modifier.fillMaxSize().padding(28.dp), contentAlignment = Alignment.BottomEnd) {
            FloatingActionButton(
                onClick = { onOpen(null) },
                containerColor = WellnessPalette.Sage500,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(72.dp)
            ) {
                Icon(Icons.Default.Add, null, Modifier.size(32.dp))
            }
        }
    }
}

@Composable
private fun ThreadBubble(thread: NarrativeThread, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.size(width = 150.dp, height = 130.dp),
        color = Color.White.copy(alpha = 0.08f),
        shape = RoundedCornerShape(32.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Icon(Icons.Default.AutoAwesome, null, Modifier.size(24.dp), tint = Color.Cyan)
            Column {
                Text(thread.label, style = MaterialTheme.typography.titleSmall, color = Color.White, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text("${thread.entryCount} entries", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.4f))
            }
        }
    }
}

@Composable
private fun LiquidEntryCard(summary: JournalSummary, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        color = Color.White.copy(alpha = 0.05f),
        shape = RoundedCornerShape(32.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Row(Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            Column(horizontalAlignment = Alignment.Start) {
                Text(formatDay(summary.createdAt), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = Color.White)
                Text(formatMonth(summary.createdAt).uppercase(), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, color = Color.White.copy(alpha = 0.4f))
            }
            Column(Modifier.weight(1f)) {
                Text(summary.title, style = MaterialTheme.typography.titleMedium, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${summary.wordCount} words • ${formatTime(summary.createdAt)}", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.4f))
            }
            Icon(Icons.Default.ChevronRight, null, tint = Color.White.copy(alpha = 0.2f))
        }
    }
}

private fun formatDay(millis: Long): String = SimpleDateFormat("dd", Locale.getDefault()).format(Date(millis))
private fun formatMonth(millis: Long): String = SimpleDateFormat("MMM", Locale.getDefault()).format(Date(millis))
private fun formatTime(millis: Long): String = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(millis))
