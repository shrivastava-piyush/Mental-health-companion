package com.wellness.companion.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.wellness.companion.data.db.DailyMoodBucket
import com.wellness.companion.ui.theme.WellnessPalette
import kotlin.math.max
import kotlin.math.min

@Composable
fun MoodTrendChart(
    buckets: List<DailyMoodBucket>,
    modifier: Modifier = Modifier,
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp),
    ) {
        if (buckets.size < 2) {
            drawEmptyState(Color.White.copy(alpha = 0.1f))
            return@Canvas
        }

        val padding = 12.dp.toPx()
        val plot = Size(size.width - padding * 2, size.height - padding * 2)

        // 1. Minimal Grid
        drawLine(
            color = Color.White.copy(alpha = 0.05f),
            start = Offset(padding, padding + plot.height * 0.1f),
            end = Offset(size.width - padding, padding + plot.height * 0.1f),
            strokeWidth = 1.dp.toPx()
        )
        drawLine(
            color = Color.White.copy(alpha = 0.05f),
            start = Offset(padding, padding + plot.height * 0.9f),
            end = Offset(size.width - padding, padding + plot.height * 0.9f),
            strokeWidth = 1.dp.toPx()
        )

        val points = buckets.toPoints(padding, plot)

        // 2. Soft area fill
        val fillPath = Path().apply {
            moveTo(points.first().x, size.height)
            for (p in points) lineTo(p.x, p.y)
            lineTo(points.last().x, size.height)
            close()
        }
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(WellnessPalette.Sage500.copy(alpha = 0.2f), WellnessPalette.Sage500.copy(alpha = 0f)),
                startY = padding,
                endY = size.height,
            ),
        )

        // 3. Main Trend Line
        val strokePath = Path().apply {
            moveTo(points[0].x, points[0].y)
            for (p in points.drop(1)) lineTo(p.x, p.y)
        }
        drawPath(
            path = strokePath,
            color = WellnessPalette.Sage500,
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
        )

        // 4. Glowing Dots
        points.forEach { pt ->
            drawCircle(color = WellnessPalette.Sage500.copy(alpha = 0.15f), radius = 7.dp.toPx(), center = pt)
            drawCircle(color = WellnessPalette.Sage500, radius = 3.dp.toPx(), center = pt)
        }
    }
}

private fun List<DailyMoodBucket>.toPoints(padding: Float, plot: Size): List<Offset> {
    // Parity with iOS: valence is mapped -100 to 100 in database for Android?
    // Let's check MoodEntry.kt. 
    // Wait, iOS uses Int(valence * 100) so -100...100.
    // Let's assume Android uses the same or -2..2. 
    // Based on original code minV = -2f, maxV = 2f. 
    // I'll stick to 0..4 range for arousal and -2..2 for valence if that's what was there.
    // Actually, I updated MoodScreen to save (v * 100).
    val minV = -100f
    val maxV = 100f
    val stepX = if (size > 1) plot.width / (size - 1) else 0f
    return mapIndexed { i, b ->
        val normalised = ((b.avgValence.toFloat() - minV) / (maxV - minV)).coerceIn(0f, 1f)
        val y = padding + plot.height * (0.8f - normalised * 0.6f) // Map to center area
        Offset(padding + stepX * i, y)
    }
}

private fun DrawScope.drawEmptyState(color: Color) {
    val y = size.height / 2f
    drawLine(color = color, start = Offset(0f, y), end = Offset(size.width, y), strokeWidth = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f)))
}
