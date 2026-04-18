package com.wellness.companion.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wellness.companion.data.llm.ModelManager

@Composable
fun ModelDownloadCard(
    status: ModelManager.Status,
    onDownload: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        modifier = modifier.fillMaxWidth().animateContentSize(),
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(
                "Reflection engine",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "A small on-device model that asks you deeper questions about your entries. " +
                    "Nothing leaves your phone.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            )

            Spacer(Modifier.height(12.dp))

            when (status) {
                is ModelManager.Status.NotDownloaded -> {
                    Button(onClick = onDownload) {
                        Text("Download (~600 MB)")
                    }
                }
                is ModelManager.Status.Downloading -> {
                    LinearProgressIndicator(
                        progress = { status.progress },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "${(status.progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                is ModelManager.Status.Ready -> {
                    Row {
                        Text(
                            "Ready",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f).padding(top = 8.dp),
                        )
                        OutlinedButton(onClick = onDelete) {
                            Text("Remove")
                        }
                    }
                }
                is ModelManager.Status.Error -> {
                    Text(
                        status.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = onDownload) {
                        Text("Retry")
                    }
                }
            }
        }
    }
}
