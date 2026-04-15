package com.wellness.companion.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import com.wellness.companion.data.db.MetricSnapshot
import com.wellness.companion.data.db.entities.MetricType

/**
 * Dense, compact bar rail showing the latest value per metric type. One
 * Canvas per row, no shared state, so recomposing a single row never triggers
 * its siblings to re-layout.
 */
@Composable
fun MetricBars(snapshots: List<MetricSnapshot>, modifier: Modifier = Modifier) {
    val byType: Map<String, MetricSnapshot> = snapshots.associateBy { it.type }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        MetricType.entries.forEach { type ->
            MetricRow(type, byType[type.name])
        }
    }
}

@Composable
private fun MetricRow(type: MetricType, snapshot: MetricSnapshot?) {
    val scheme = MaterialTheme.colorScheme
    val value = snapshot?.value ?: 0.0
    val target = targetFor(type)
    val fraction = (value / target).toFloat().coerceIn(0f, 1f)
    val barColor = when (type) {
        MetricType.SLEEP_HOURS       -> scheme.tertiary
        MetricType.ACTIVITY_MINUTES  -> scheme.primary
        MetricType.HYDRATION_LITRES  -> scheme.secondary
        MetricType.STEPS             -> scheme.primary
        MetricType.MEDITATION_MINUTES -> scheme.tertiary
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            type.displayName,
            style = MaterialTheme.typography.labelLarge,
            color = scheme.onSurfaceVariant,
            modifier = Modifier.width(96.dp),
        )
        Canvas(
            modifier = Modifier
                .weight(1f)
                .height(14.dp),
        ) {
            drawBar(fraction, barColor, scheme.surfaceVariant)
        }
        Spacer(Modifier.width(12.dp))
        Text(
            if (snapshot != null) "${format(value)}${type.unit}" else "—",
            style = MaterialTheme.typography.labelLarge,
            color = scheme.onSurface,
            modifier = Modifier.width(64.dp),
        )
    }
}

private fun DrawScope.drawBar(
    fraction: Float,
    fill: androidx.compose.ui.graphics.Color,
    track: androidx.compose.ui.graphics.Color,
) {
    val radius = CornerRadius(size.height / 2f)
    drawRoundRect(color = track, size = size, cornerRadius = radius)
    drawRoundRect(
        color = fill,
        size = Size(size.width * fraction, size.height),
        topLeft = Offset.Zero,
        cornerRadius = radius,
    )
}

private fun targetFor(type: MetricType): Double = when (type) {
    MetricType.SLEEP_HOURS        -> 8.0
    MetricType.ACTIVITY_MINUTES   -> 30.0
    MetricType.HYDRATION_LITRES   -> 2.0
    MetricType.STEPS              -> 10_000.0
    MetricType.MEDITATION_MINUTES -> 20.0
}

private fun format(v: Double): String =
    if (v >= 100) v.toInt().toString() else "%.1f".format(v)
