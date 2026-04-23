package com.wellness.companion.ui.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import com.wellness.companion.ui.MoodCategory
import com.wellness.companion.ui.WellnessContentProvider
import com.wellness.companion.ui.components.LiquidAura
import com.wellness.companion.ui.theme.WellnessPalette
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onOpenMood: () -> Unit,
    onOpenJournal: () -> Unit,
    onOpenReflection: (Long?, String) -> Unit,
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
            Spacer(Modifier.height(60.dp))

            // 1. Animated Adaptive Quote
            AnimatedQuoteHero(
                quote = state.quote.first,
                author = state.quote.second,
                category = MoodCategory.fromValence(state.recentMood?.valence ?: 0)
            )

            Spacer(Modifier.height(60.dp))

            // 2. Reflection Sparks
            SectionHeader("Sparks")
            Spacer(Modifier.height(16.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                state.aiSpark?.let { spark ->
                    item { SparkCard(spark, isAi = true) { onOpenReflection(null, spark) } }
                }
                item { SparkCard("What does peace look like for you right now?") { onOpenReflection(null, "") } }
                item { SparkCard("Describe a color that matches your energy.") { onOpenReflection(null, "") } }
            }

            Spacer(Modifier.height(24.dp))

            // 3. Redesigned Non-Blocking Check-in
            SanctuaryCheckInCard(onClick = onOpenMood)

            Spacer(Modifier.height(40.dp))

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
private fun AnimatedQuoteHero(quote: String, author: String, category: MoodCategory) {
    val words = remember(quote) { quote.split(" ") }
    var revealProgress by remember { mutableStateOf(0f) }
    
    LaunchedEffect(quote) {
        revealProgress = 0f
        animate(0f, 1f, animationSpec = tween(2500, easing = LinearOutSlowInEasing)) { value, _ ->
            revealProgress = value
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        Icon(
            Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.4f),
            modifier = Modifier.size(30.dp)
        )

        @OptIn(ExperimentalLayoutApi::class)
        FlowRow(
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            words.forEachIndexed { index, word ->
                val wordStep = 1f / words.size
                val wordStart = index * wordStep
                val wordOpacity = ((revealProgress - wordStart) / wordStep).coerceIn(0f, 1f)
                val blur = (1f - wordOpacity) * 10f
                val scale = 0.95f + (wordOpacity * 0.05f)

                Text(
                    text = " ",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Medium,
                        fontStyle = FontStyle.Italic,
                        lineHeight = 36.sp
                    ),
                    color = Color.White,
                    modifier = Modifier
                        .graphicsLayer(
                            alpha = wordOpacity,
                            scaleX = scale,
                            scaleY = scale,
                            renderEffect = if (blur > 0.1f) BlurEffect(blur, blur) else null
                        )
                )
            }
        }

        Text(
            text = author.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp,
            color = Color.White.copy(alpha = 0.4f),
            modifier = Modifier.alpha(if (revealProgress > 0.8f) 1f else 0f)
        )
    }
}

@Composable
private fun SanctuaryCheckInCard(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = Color.White.copy(alpha = 0.08f),
        shape = RoundedCornerShape(32.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(Color.White.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Spa, 
                    null, 
                    tint = WellnessPalette.Sage500,
                    modifier = Modifier.size(28.dp)
                )
            }
            
            Column(Modifier.weight(1f)) {
                Text("Sanctuary Check-in", fontWeight = FontWeight.Bold, color = Color.White)
                Text("Observe your inner state", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.5f))
            }
            
            Icon(Icons.Default.ChevronRight, null, tint = Color.White.copy(alpha = 0.3f))
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

private fun Color.Companion.opacity(alpha: Float): Color = Color.White.copy(alpha = alpha)
