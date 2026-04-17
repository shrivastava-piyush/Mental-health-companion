package com.wellness.companion.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wellness.companion.domain.narrative.MirrorGenerator

/**
 * Monthly "Mirror Moment" card — a single-screen synthesis of the user's
 * emotional arc, top words, highlighted entry, and a narrative callback.
 */
@Composable
fun MirrorCard(mirror: MirrorGenerator.Mirror, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        ),
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(
                mirror.periodLabel,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "${mirror.totalMoods} mood logs \u00B7 ${mirror.totalEntries} journal entries",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f),
            )

            // Mini sparkline of the mood arc.
            if (mirror.moodArc.size >= 2) {
                Spacer(Modifier.height(12.dp))
                MoodTrendChart(
                    buckets = mirror.moodArc,
                    modifier = Modifier.height(80.dp),
                )
            }

            // Top words.
            if (mirror.topWords.isNotEmpty()) {
                Spacer(Modifier.height(14.dp))
                Text(
                    "Words on your mind",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                )
                Spacer(Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    mirror.topWords.forEach { (word, count) ->
                        Text(
                            "$word ($count)",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                        )
                    }
                }
            }

            // Highlighted entry.
            if (mirror.highlightSnippet.isNotBlank()) {
                Spacer(Modifier.height(14.dp))
                Text(
                    "Most invested entry",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "\u201C${mirror.highlightSnippet}\u2026\u201D",
                    style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    maxLines = 3,
                )
                Text(
                    "\u2014 ${mirror.highlightTitle}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f),
                )
            }

            // Callback.
            mirror.callback?.let { cb ->
                Spacer(Modifier.height(14.dp))
                Text(
                    cb,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                )
            }
        }
    }
}
