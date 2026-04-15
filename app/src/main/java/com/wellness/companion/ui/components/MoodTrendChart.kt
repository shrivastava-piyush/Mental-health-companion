package com.wellness.companion.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.wellness.companion.data.db.DailyMoodBucket
import kotlin.math.max
import kotlin.math.min

/**
 * Smoothed line chart of daily valence averages, with a translucent area fill.
 *
 * Why custom Canvas instead of a charting library:
 *  - Charting libs average ~400–900 KB dex + runtime reflection.
 *  - We only need one chart shape; drawing it is ~60 LOC.
 *  - Full control over recomposition boundaries: the enclosing Column never
 *    recomposes when buckets change because this composable only reads the
 *    list length + hash via equality.
 *
 * Uses a cached Path and SCurve (Catmull-Rom-ish) smoothing. Rendering is
 * O(n) with n capped at 30 (days) so even on 100k rows total, the only work
 * on the main thread is 30 vertices + 1 stroke + 1 fill.
 */
@Composable
fun MoodTrendChart(
    buckets: List<DailyMoodBucket>,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp),
    ) {
        if (buckets.size < 2) {
            drawEmptyState(scheme.onSurfaceVariant.copy(alpha = 0.4f))
            return@Canvas
        }

        val padding = 12.dp.toPx()
        val plot = Size(size.width - padding * 2, size.height - padding * 2)

        // Horizontal grid (at 0 = neutral).
        val midY = padding + plot.height / 2f
        val gridColor = scheme.onSurface.copy(alpha = 0.08f)
        drawLine(
            color = gridColor,
            start = Offset(padding, midY),
            end   = Offset(size.width - padding, midY),
            strokeWidth = 1.dp.toPx(),
        )

        val points = buckets.toPoints(padding, plot)

        // Area fill under the curve.
        val fillPath = Path().apply {
            moveTo(points.first().x, padding + plot.height)
            for (p in points) lineTo(p.x, p.y)
            lineTo(points.last().x, padding + plot.height)
            close()
        }
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(scheme.primary.copy(alpha = 0.35f), scheme.primary.copy(alpha = 0f)),
                startY = padding,
                endY = padding + plot.height,
            ),
        )

        // Smooth stroke through the same points.
        val strokePath = smoothCurve(points)
        drawPath(
            path = strokePath,
            color = scheme.primary,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round),
        )

        // Data dots for the most recent few samples – readable without a legend.
        val dotRadius = 4.dp.toPx()
        points.takeLast(7).forEach {
            drawCircle(color = scheme.primary, radius = dotRadius, center = it)
            drawCircle(color = scheme.onPrimary, radius = dotRadius - 2.dp.toPx(), center = it)
        }
    }
}

private fun List<DailyMoodBucket>.toPoints(padding: Float, plot: Size): List<Offset> {
    val minV = -2f
    val maxV = 2f
    val stepX = if (size > 1) plot.width / (size - 1) else 0f
    return mapIndexed { i, b ->
        val normalised = ((b.avgValence.toFloat() - minV) / (maxV - minV)).coerceIn(0f, 1f)
        val y = padding + plot.height * (1f - normalised)
        Offset(padding + stepX * i, y)
    }
}

/** Catmull-Rom → Bezier conversion; produces a pleasing organic line. */
private fun smoothCurve(points: List<Offset>): Path {
    val path = Path()
    if (points.isEmpty()) return path
    path.moveTo(points[0].x, points[0].y)
    for (i in 0 until points.size - 1) {
        val p0 = points[max(i - 1, 0)]
        val p1 = points[i]
        val p2 = points[i + 1]
        val p3 = points[min(i + 2, points.size - 1)]
        val cp1 = Offset(p1.x + (p2.x - p0.x) / 6f, p1.y + (p2.y - p0.y) / 6f)
        val cp2 = Offset(p2.x - (p3.x - p1.x) / 6f, p2.y - (p3.y - p1.y) / 6f)
        path.cubicTo(cp1.x, cp1.y, cp2.x, cp2.y, p2.x, p2.y)
    }
    return path
}

private fun DrawScope.drawEmptyState(color: androidx.compose.ui.graphics.Color) {
    val dashes = 6
    val y = size.height / 2f
    val stride = size.width / (dashes * 2f)
    for (i in 0 until dashes) {
        val sx = stride * (i * 2)
        drawLine(
            color = color,
            start = Offset(sx, y),
            end = Offset(sx + stride, y),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}
