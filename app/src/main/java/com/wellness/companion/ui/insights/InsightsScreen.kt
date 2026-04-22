package com.wellness.companion.ui.insights

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wellness.companion.di.AppContainer
import com.wellness.companion.di.ViewModelFactories
import com.wellness.companion.ui.components.LiquidAura
import com.wellness.companion.ui.components.ModelDownloadCard
import com.wellness.companion.ui.components.MoodTrendChart
import com.wellness.companion.ui.theme.WellnessPalette
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun InsightsScreen(container: AppContainer, contentPadding: PaddingValues) {
    val vm: InsightsViewModel = viewModel(factory = ViewModelFactories.insights(container))
    val state by vm.state.collectAsStateWithLifecycle()
    val modelStatus by container.modelManager.status.collectAsStateWithLifecycle()

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

            // 1. Hero Header
            Column(horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("PULSE", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, letterSpacing = 2.sp, color = Color.White.copy(alpha = 0.4f))
                Text("Patterns", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Gentle observations of your journey", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.5f))
            }

            Spacer(Modifier.height(40.dp))

            // 2. High-Fidelity Stats
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp), modifier = Modifier.fillMaxWidth()) {
                LiquidStatCard(label = "Logs", value = "${state.totalMoods}", icon = Icons.Default.Mood, color = WellnessPalette.LiquidTeal, modifier = Modifier.weight(1f))
                LiquidStatCard(label = "Notes", value = "${state.totalJournals}", icon = Icons.Default.TextSnippet, color = WellnessPalette.LiquidRose, modifier = Modifier.weight(1f))
            }

            Spacer(Modifier.height(40.dp))

            // 3. AI Perspective
            if (state.patternNarrative.isNotBlank()) {
                SectionHeader("Perspective")
                Spacer(Modifier.height(20.dp))
                Surface(
                    color = Color.White.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(40.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(32.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        Icon(Icons.Default.FormatQuote, null, Modifier.size(32.dp).rotate(180f), tint = Color.White.copy(alpha = 0.2f))
                        Text(
                            text = state.patternNarrative,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontFamily = FontFamily.Serif,
                                fontStyle = FontStyle.Italic,
                                lineHeight = 32.sp,
                                fontSize = 20.sp
                            ),
                            color = Color.White
                        )
                    }
                }
                Spacer(Modifier.height(40.dp))
            }

            // 4. Emotional Flow
            SectionHeader("Emotional Flow")
            Spacer(Modifier.height(24.dp))
            Surface(
                color = Color.White.copy(alpha = 0.05f),
                shape = RoundedCornerShape(40.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(24.dp)) {
                    MoodTrendChart(buckets = state.trend, modifier = Modifier.height(180.dp))
                }
            }

            Spacer(Modifier.height(40.dp))

            // 5. Intelligence Status
            ModelDownloadCard(
                status = modelStatus,
                onDownload = { vm.downloadModel(container.modelManager) },
                onDelete = { container.modelManager.deleteModel() },
            )

            Spacer(Modifier.height(150.dp))
        }
    }
}

@Composable
private fun LiquidStatCard(label: String, value: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Surface(
        color = Color.White.copy(alpha = 0.08f),
        shape = RoundedCornerShape(32.dp),
        modifier = modifier
    ) {
        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(Modifier.size(44.dp).background(color.copy(alpha = 0.2f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = color)
            }
            Column {
                Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = Color.White)
                Text(label.uppercase(), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, letterSpacing = 2.sp, color = Color.White.copy(alpha = 0.4f))
            }
        }
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
