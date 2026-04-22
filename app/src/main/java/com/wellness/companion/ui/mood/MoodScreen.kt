package com.wellness.companion.ui.mood

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wellness.companion.di.AppContainer
import com.wellness.companion.di.ViewModelFactories
import com.wellness.companion.ui.components.LiquidAura
import com.wellness.companion.ui.components.WellnessWheel
import com.wellness.companion.ui.theme.WellnessPalette

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MoodScreen(
    container: AppContainer,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val viewModel: MoodViewModel = viewModel(factory = ViewModelFactories.mood(container))
    val state by viewModel.state.collectAsState()
    
    val qualities = listOf("Peaceful", "Joyful", "Balanced", "Empowered", "Grateful", "Tired", "Worried", "Frustrated", "Overwhelmed", "Numb")
    var selectedQualities by remember { mutableStateOf(setOf<String>()) }

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

            // 1. Immersive Header
            Text(
                text = "How are you, right now?",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "A moment to breathe and observe.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.5f)
            )

            Spacer(Modifier.height(40.dp))

            // 2. The Liquid Wheel
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(40.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "MAP YOUR ENERGY",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    color = Color.White.copy(alpha = 0.4f)
                )
                
                WellnessWheel(
                    valence = state.draft.valence / 100.0,
                    arousal = state.draft.arousal / 100.0,
                    onChange = { v, a ->
                        viewModel.send(MoodViewModel.Intent.UpdateValence((v * 100).toInt()))
                        viewModel.send(MoodViewModel.Intent.UpdateArousal((a * 100).toInt()))
                    },
                    modifier = Modifier.size(320.dp)
                )
            }

            Spacer(Modifier.height(40.dp))

            // 3. Qualities
            Text(
                text = "DESCRIBE THE TEXTURE",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                color = Color.White.copy(alpha = 0.5f)
            )
            
            Spacer(Modifier.height(20.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                qualities.forEach { quality ->
                    val isSelected = selectedQualities.contains(quality)
                    Surface(
                        onClick = {
                            selectedQualities = if (isSelected) selectedQualities - quality else selectedQualities + quality
                            viewModel.send(MoodViewModel.Intent.UpdateLabel(selectedQualities.sorted().joinToString(", ")))
                        },
                        color = if (isSelected) WellnessPalette.Sage500 else Color.White.copy(alpha = 0.1f),
                        shape = CircleShape,
                        border = if (isSelected) null else BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                    ) {
                        Text(
                            text = quality,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(Modifier.height(40.dp))

            // 4. Save Button
            Button(
                onClick = { viewModel.send(MoodViewModel.Intent.SaveMood) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = WellnessPalette.Sage500)
            ) {
                Text("Log Check-in", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(120.dp))
        }
    }
}
