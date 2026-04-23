package com.wellness.companion.ui.journal

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import coil.compose.AsyncImage
import com.wellness.companion.data.db.JournalSummary
import com.wellness.companion.di.AppContainer
import com.wellness.companion.ui.WellnessContentProvider
import com.wellness.companion.ui.components.LiquidAura
import com.wellness.companion.ui.theme.WellnessPalette
import kotlinx.coroutines.launch

@Composable
fun JournalListScreen(
    container: AppContainer,
    onOpen: (Long) -> Unit,
    onOpenThread: (Long, String) -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val viewModel: JournalListViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = com.wellness.companion.di.ViewModelFactories.journalList(container)
    )
    val state by viewModel.state.collectAsState()
    
    var synthesizedInsight by remember { mutableStateOf<String?>(null) }
    var isSynthesizing by remember { mutableStateOf(false) }

    LaunchedEffect(state.entries.size) {
        if (state.entries.size >= 2 && synthesizedInsight == null) {
            isSynthesizing = true
            val fullEntries = container.journalRepository.fetchRecentEntries(3)
            synthesizedInsight = container.reflectionEngine?.synthesizeInsight(fullEntries)
            isSynthesizing = false
        }
    }

    Box(Modifier.fillMaxSize()) {
        LiquidAura()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(contentPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp)
        ) {
            Spacer(Modifier.height(20.dp))

            // 1. High-Fidelity Hero Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(48.dp))
            ) {
                AsyncImage(
                    model = WellnessContentProvider.LIBRARY_HERO,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                            )
                        )
                )
                Column(
                    Modifier
                        .align(Alignment.BottomStart)
                        .padding(32.dp)
                ) {
                    Text("REFLECTIONS", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f), letterSpacing = 2.sp)
                    Text("Library", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black, color = Color.White)
                }
            }
            
            Text(
                text = WellnessContentProvider.ATTRIBUTION,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.2f),
                modifier = Modifier.padding(top = 8.dp, start = 12.dp)
            )

            Spacer(Modifier.height(40.dp))

            // 2. Intelligence Layer: Synthesis Card
            if (state.entries.isNotEmpty()) {
                SynthesisSection(insight = synthesizedInsight, isSynthesizing = isSynthesizing) {
                    isSynthesizing = true
                    val fullEntries = container.journalRepository.fetchRecentEntries(3)
                    synthesizedInsight = container.reflectionEngine?.synthesizeInsight(fullEntries)
                    isSynthesizing = false
                }
                Spacer(Modifier.height(50.dp))
            }

            // 3. Themes
            if (state.threads.isNotEmpty()) {
                Text("THEMES", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, color = Color.White.copy(alpha = 0.5f))
                Spacer(Modifier.height(20.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(state.threads) { thread ->
                        Surface(
                            onClick = { onOpenThread(thread.id, thread.label) },
                            modifier = Modifier.size(150.dp, 130.dp),
                            color = Color.White.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(32.dp),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                        ) {
                            Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                Icon(Icons.Default.AutoAwesome, null, tint = Color.Cyan, modifier = Modifier.size(24.dp))
                                Column {
                                    Text(thread.label, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text("${thread.entryCount} entries", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.4f))
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(50.dp))
            }

            // 4. Timeline
            Text("TIMELINE", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, color = Color.White.copy(alpha = 0.5f))
            Spacer(Modifier.height(20.dp))
            state.entries.forEach { summary ->
                RecentActivityRow(summary, onOpen)
                Spacer(Modifier.height(16.dp))
            }

            Spacer(Modifier.height(150.dp))
        }

        // Floating Plus Button
        FloatingActionButton(
            onClick = { onOpen(-1L) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(28.dp)
                .padding(bottom = 80.dp),
            containerColor = WellnessPalette.LiquidIndigo,
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Icon(Icons.Default.Add, null, modifier = Modifier.size(30.dp))
        }
    }
}

@Composable
private fun SynthesisSection(insight: String?, isSynthesizing: Boolean, onRefresh: suspend () -> Unit) {
    val scope = rememberCoroutineScope()
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("SYNTESIZED INSIGHT", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, color = Color.White.copy(alpha = 0.5f))
            Spacer(Modifier.weight(1f))
            if (isSynthesizing) {
                CircularProgressIndicator(Modifier.size(16.dp), color = Color.Cyan, strokeWidth = 2.dp)
            } else {
                IconButton(onClick = { scope.launch { onRefresh() } }) {
                    Icon(Icons.Default.Refresh, null, tint = Color.Cyan, modifier = Modifier.size(16.dp))
                }
            }
        }
        
        Surface(
            color = Color.White.copy(alpha = 0.08f),
            shape = RoundedRectangle(40.dp),
            border = BorderStroke(1.dp, Color.Cyan.copy(alpha = 0.2f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(Modifier.padding(32.dp)) {
                if (insight != null) {
                    Text(insight, style = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.Serif), fontStyle = FontStyle.Italic, color = Color.White)
                } else {
                    Text(
                        if (isSynthesizing) "Finding the hidden thread…" else "Analysis of your last 3 reflections.",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isSynthesizing) Color.Cyan.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.4f)
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentActivityRow(summary: JournalSummary, onClick: (Long) -> Unit) {
    Surface(
        onClick = { onClick(summary.id) },
        color = Color.White.copy(alpha = 0.05f),
        shape = RoundedRectangle(32.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            Icon(Icons.Default.TextSnippet, null, tint = Color.Cyan, modifier = Modifier.size(24.dp))
            Column {
                Text(summary.title, fontWeight = FontWeight.Bold, color = Color.White)
                Text("${summary.wordCount} words", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.4f))
            }
            Spacer(Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, null, tint = Color.White.copy(alpha = 0.2f))
        }
    }
}

private fun RoundedRectangle(radius: Dp) = RoundedCornerShape(radius)
