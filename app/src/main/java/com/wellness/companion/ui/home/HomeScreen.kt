package com.wellness.companion.ui.home

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import com.wellness.companion.ui.components.LiquidAura
import com.wellness.companion.ui.theme.WellnessPalette
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onOpenMood: () -> Unit,
    onOpenJournal: () -> Unit,
    onOpenReflection: (Long?) -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val state by viewModel.state.collectAsState()

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
            Spacer(Modifier.height(40.dp))

            // 1. Hero Quote
            QuoteHero(state.quote.first, state.quote.second)

            Spacer(Modifier.height(50.dp))

            // 2. Greeting & Actions
            Text(
                text = state.greeting,
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                HomeActionCard(
                    title = "Check-in",
                    icon = Icons.Outlined.Mood,
                    color = WellnessPalette.Sage500,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenMood
                )
                HomeActionCard(
                    title = "Reflect",
                    icon = Icons.Outlined.EditNote,
                    color = WellnessPalette.Teal300,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenJournal
                )
            }

            Spacer(Modifier.height(40.dp))

            // 3. Reflection Sparks
            SectionHeader("Reflection Sparks")
            Spacer(Modifier.height(16.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                state.aiSpark?.let { spark ->
                    item { SparkCard(spark, isAi = true) { onOpenReflection(null) } }
                }
                item { SparkCard("What brought you a sense of calm this morning?") { onOpenReflection(null) } }
                item { SparkCard("Is there a small victory you can celebrate today?") { onOpenReflection(null) } }
            }

            Spacer(Modifier.height(20.dp))

            // 4. Recent Activity
            if (state.recentMood != null || state.recentJournal != null) {
                SectionHeader("Recent Activity")
                Spacer(Modifier.height(20.dp))

                state.recentMood?.let { mood ->
                    RecentActivityRow(
                        icon = { Text(moodEmoji(mood.valence), fontSize = 28.sp) },
                        title = mood.label.ifBlank { "Mood Logged" },
                        subtitle = formatRelativeDate(mood.createdAt)
                    )
                    Spacer(Modifier.height(16.dp))
                }

                state.recentJournal?.let { journal ->
                    RecentActivityRow(
                        icon = { Icon(Icons.Default.TextSnippet, null, tint = WellnessPalette.Teal300) },
                        title = journal.title,
                        subtitle = formatRelativeDate(journal.createdAt)
                    )
                }
            }

            Spacer(Modifier.height(120.dp))
        }
    }
}

@Composable
private fun QuoteHero(text: String, author: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(40.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(40.dp))
            .padding(vertical = 40.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Icon(
            Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = WellnessPalette.Sage500.copy(alpha = 0.6f),
            modifier = Modifier.size(30.dp)
        )

        Text(
            text = text,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Medium,
                fontStyle = FontStyle.Italic,
                lineHeight = 36.sp
            ),
            textAlign = TextAlign.Center,
            color = Color.White
        )

        Text(
            text = author.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp,
            color = Color.White.copy(alpha = 0.5f)
        )
    }
}

@Composable
private fun HomeActionCard(
    title: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(140.dp),
        color = Color.White.copy(alpha = 0.08f),
        shape = RoundedCornerShape(32.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(color, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = Color.White)
            }
            Text(title, style = MaterialTheme.typography.titleMedium, color = Color.White)
        }
    }
}

@Composable
private fun SparkCard(text: String, isAi: Boolean = false, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.size(width = 220.dp, height = 130.dp),
        color = Color.White.copy(alpha = 0.08f),
        shape = RoundedCornerShape(32.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (isAi) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.AutoAwesome, null, Modifier.size(10.dp), tint = Color.Cyan)
                    Text("AI MUSING", style = MaterialTheme.typography.labelSmall, color = Color.Cyan, fontWeight = FontWeight.Black)
                }
            }
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Serif),
                color = Color.White,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun RecentActivityRow(icon: @Composable () -> Unit, title: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Box(
            modifier = Modifier.size(44.dp),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
        Column {
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = Color.White)
            Text(subtitle, style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.4f))
        }
        Spacer(Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, null, tint = Color.White.copy(alpha = 0.2f))
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Black,
        letterSpacing = 2.sp,
        color = Color.White.copy(alpha = 0.5f)
    )
}

private fun moodEmoji(v: Int): String = when {
    v > 60 -> "✨"
    v > 20 -> "🙂"
    v > -20 -> "😐"
    v > -60 -> "🙁"
    else -> "😔"
}

private fun formatRelativeDate(millis: Long): String {
    val date = Date(millis)
    val formatter = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
    return formatter.format(date)
}
