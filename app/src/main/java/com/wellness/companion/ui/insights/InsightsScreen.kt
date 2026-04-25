package com.wellness.companion.ui.insights

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wellness.companion.di.AppContainer
import com.wellness.companion.ui.components.LiquidAura
import com.wellness.companion.ui.components.MoodTrendChart
import com.wellness.companion.ui.theme.WellnessPalette
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@Composable
fun InsightsScreen(container: AppContainer, contentPadding: PaddingValues = PaddingValues(0.dp)) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val viewModel: InsightsViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = com.wellness.companion.di.ViewModelFactories.insights(container)
    )
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    
    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                context.contentResolver.openInputStream(it)?.use { input ->
                    val file = File(context.filesDir, "custom_background.jpg")
                    FileOutputStream(file).use { output -> input.copyTo(output) }
                }
            }
        }
    )

    Box(Modifier.fillMaxSize()) {
        LiquidAura()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(contentPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp) // Strict Sanctuary Margin
        ) {
            Spacer(Modifier.height(40.dp))

            // 1. Personalization Header
            Column(horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Text("PULSE", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.4f), letterSpacing = 2.sp)
                Text("Patterns", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black, color = Color.White)
                
                Button(
                    onClick = { photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(50)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.PhotoLibrary, null, modifier = Modifier.size(16.dp))
                        Text("Personalize Background", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(50.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                StatCard(label = "Logs", value = uiState.totalMoods.toString(), icon = Icons.Default.Face, color = WellnessPalette.LiquidTeal, modifier = Modifier.weight(1f))
                StatCard(label = "Notes", value = uiState.totalJournals.toString(), icon = Icons.Default.TextSnippet, color = WellnessPalette.LiquidRose, modifier = Modifier.weight(1f))
            }
            
            if (uiState.patternNarrative.isNotBlank()) {
                Spacer(Modifier.height(40.dp))
                Text("PERSPECTIVE", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, color = Color.White.copy(alpha = 0.5f))
                Spacer(Modifier.height(20.dp))
                Surface(
                    color = Color.White.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(40.dp)
                ) {
                    Column(Modifier.padding(32.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        Icon(Icons.Default.FormatQuote, null, tint = Color.White.copy(alpha = 0.2f), modifier = Modifier.size(32.dp))
                        Text(uiState.patternNarrative, style = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.Serif), fontStyle = FontStyle.Italic, color = Color.White)
                    }
                }
            }

            Spacer(Modifier.height(40.dp))
            Text("EMOTIONAL FLOW", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, color = Color.White.copy(alpha = 0.5f))
            Spacer(Modifier.height(20.dp))
            Surface(
                color = Color.White.copy(alpha = 0.05f),
                shape = RoundedCornerShape(40.dp),
                modifier = Modifier.fillMaxWidth().height(220.dp)
            ) {
                Box(Modifier.padding(24.dp)) {
                    MoodTrendChart(buckets = uiState.trend)
                }
            }

            Spacer(Modifier.height(40.dp))
            ModelDownloadCard(container.modelManager) {
                scope.launch { container.modelManager.download("https://huggingface.co/TheBloke/TinyLlama-1.1B-Chat-v1.0-GGUF/resolve/main/tinyllama-1.1b-chat-v1.0.Q4_K_M.gguf") }
            }
            
            Spacer(Modifier.height(150.dp))
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = Color.White.copy(alpha = 0.08f),
        shape = RoundedCornerShape(32.dp)
    ) {
        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(Modifier.size(44.dp).background(color.copy(alpha = 0.2f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = color)
            }
            Column {
                Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = Color.White)
                Text(label.uppercase(), style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.4f), letterSpacing = 2.sp)
            }
        }
    }
}

@Composable
private fun ModelDownloadCard(manager: com.wellness.companion.data.llm.ModelManager, onDownload: () -> Unit) {
    val status by manager.status.collectAsStateWithLifecycle()
    Surface(
        color = Color.White.copy(alpha = 0.05f),
        shape = RoundedCornerShape(32.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(28.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
             Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("THE MIRROR", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.4f), letterSpacing = 2.sp)
                    Text("Offline Intelligence", fontWeight = FontWeight.Bold, color = Color.White)
                }
                Icon(Icons.Default.Psychology, null, tint = Color.Cyan)
            }
            
            when (val s = status) {
                is com.wellness.companion.data.llm.ModelManager.Status.NotDownloaded -> {
                    Button(
                        onClick = onDownload,
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = WellnessPalette.LiquidIndigo),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text("Download Assistant", fontWeight = FontWeight.Bold)
                    }
                }
                is com.wellness.companion.data.llm.ModelManager.Status.Downloading -> {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row {
                            Text("Synchronizing…", style = MaterialTheme.typography.labelMedium, color = Color.Cyan, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.weight(1f))
                            Text("${(s.progress * 100).toInt()}%", style = MaterialTheme.typography.labelMedium, color = Color.White, fontWeight = FontWeight.Black)
                        }
                        LinearProgressIndicator(progress = { s.progress }, modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(50)), color = Color.Cyan, trackColor = Color.White.copy(alpha = 0.1f))
                    }
                }
                is com.wellness.companion.data.llm.ModelManager.Status.Ready -> {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.CheckCircle, null, tint = Color.Cyan)
                        Text("Mirror Ready", fontWeight = FontWeight.Bold, color = Color.Cyan)
                    }
                }
                else -> {}
            }
        }
    }
}
