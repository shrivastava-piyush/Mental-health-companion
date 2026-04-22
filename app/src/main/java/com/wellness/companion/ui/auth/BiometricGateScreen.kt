package com.wellness.companion.ui.auth

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
import com.wellness.companion.ui.components.LiquidAura
import com.wellness.companion.ui.theme.WellnessPalette

@Composable
fun BiometricGateScreen(onUnlocked: () -> Unit) {
    val context = LocalContext.current
    var error by remember { mutableStateOf<String?>(null) }

    Box(Modifier.fillMaxSize()) {
        LiquidAura()

        Column(
            modifier = Modifier.fillMaxSize().padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(48.dp)
        ) {
            Spacer(Modifier.weight(1f))

            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(24.dp)) {
                ZStack {
                    Surface(
                        modifier = Modifier.size(160.dp),
                        shape = CircleShape,
                        color = WellnessPalette.Sage500.copy(alpha = 0.1f)
                    ) {}
                    Icon(Icons.Default.Lock, null, Modifier.size(80.dp), tint = WellnessPalette.Sage500)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Wellness Companion", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Your private sanctuary.", style = MaterialTheme.typography.bodyLarge, fontFamily = FontFamily.Serif, fontStyle = FontStyle.Italic, color = Color.White.copy(alpha = 0.6f))
                }
            }

            Spacer(Modifier.weight(1f))

            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Button(
                    onClick = { onUnlocked() },
                    modifier = Modifier.fillMaxWidth().height(64.dp),
                    shape = androidx.compose.foundation.shape.CircleShape, // Capsule-like with CircleShape in high-height button
                    colors = ButtonDefaults.buttonColors(containerColor = WellnessPalette.Sage500)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Default.Fingerprint, null)
                        Text("Unlock Sanctuary", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                }

                error?.let {
                    Text(it, color = Color.Red.copy(alpha = 0.8f), style = MaterialTheme.typography.labelSmall)
                }
            }

            Spacer(Modifier.weight(1f))

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Shield, null, Modifier.size(14.dp), tint = Color.White.copy(alpha = 0.3f))
                Text("LOCAL ENCRYPTION ACTIVE", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, letterSpacing = 2.sp, color = Color.White.copy(alpha = 0.3f))
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun ZStack(content: @Composable () -> Unit) {
    Box(contentAlignment = Alignment.Center) { content() }
}
