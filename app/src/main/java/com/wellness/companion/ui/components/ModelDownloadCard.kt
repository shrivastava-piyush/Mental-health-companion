package com.wellness.companion.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.wellness.companion.data.llm.ModelManager
import com.wellness.companion.ui.theme.WellnessPalette

@Composable
fun ModelDownloadCard(
    status: ModelManager.Status,
    onDownload: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = Color.White.copy(alpha = 0.05f),
        shape = RoundedCornerShape(32.dp),
        modifier = modifier.fillMaxWidth().animateContentSize()
    ) {
        Column(Modifier.padding(28.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("REFLECTION ENGINE", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, letterSpacing = 2.sp, color = Color.White.copy(alpha = 0.4f))
                    Text("Offline Intelligence", style = MaterialTheme.typography.headlineSmall, color = Color.White, fontWeight = FontWeight.Bold)
                    Text("Private, on-device AI reflection helper", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.5f))
                }
                Icon(Icons.Default.Psychology, null, Modifier.size(32.dp), tint = Color.Cyan)
            }

            when (status) {
                is ModelManager.Status.NotDownloaded -> {
                    Button(
                        onClick = onDownload,
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = WellnessPalette.LiquidIndigo)
                    ) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Download Assistant", fontWeight = FontWeight.Bold)
                            Text("600 MB", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.6f))
                        }
                    }
                }
                is ModelManager.Status.Downloading -> {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                progress = { status.progress },
                                color = Color.Cyan,
                                trackColor = Color.White.copy(alpha = 0.1f),
                                strokeWidth = 4.dp,
                                modifier = Modifier.size(44.dp)
                            )
                            Text("${(status.progress * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, color = Color.White, fontSize = 8.sp)
                        }
                        Text("Loading Intelligence…", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.6f))
                    }
                }
                is ModelManager.Status.Ready -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.CheckCircle, null, tint = Color.Cyan, modifier = Modifier.size(20.dp))
                            Text("Engine Ready", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.Cyan)
                        }
                        TextButton(onClick = onDelete) {
                            Text("Remove", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.4f))
                        }
                    }
                }
                is ModelManager.Status.Error -> {
                    Column(horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(status.message, color = Color.Red, style = MaterialTheme.typography.labelSmall)
                        Button(onClick = onDownload, colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f))) {
                            Text("Retry Download", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
