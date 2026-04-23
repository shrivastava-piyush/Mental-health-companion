package com.wellness.companion.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.wellness.companion.ui.theme.WellnessPalette
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun LiquidAura(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "aura")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    // Breathing rhythm (8 second cycle)
    val breath by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breath"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Base background
        drawRect(WellnessPalette.LiquidDeep)

        val globalScale = 1.0f + (breath * 0.15f)
        val globalAlpha = 0.6f + (breath * 0.4f)

        // Drifting liquid blobs with high displacement
        drawLiquidBlob(WellnessPalette.LiquidIndigo, Offset(w * 0.2f, h * 0.3f), w * 0.8f, time, 0f, globalScale, globalAlpha)
        drawLiquidBlob(WellnessPalette.LiquidTeal, Offset(w * 0.8f, h * 0.6f), w * 0.7f, time, 2.5f, globalScale, globalAlpha)
        drawLiquidBlob(WellnessPalette.LiquidRose, Offset(w * 0.3f, h * 0.9f), w * 0.75f, time, 4.2f, globalScale, globalAlpha)
        drawLiquidBlob(WellnessPalette.LiquidAmber, Offset(w * 0.7f, h * 0.1f), w * 0.6f, time, 1.1f, globalScale, globalAlpha)
    }
}

private fun DrawScope.drawLiquidBlob(
    color: Color,
    center: Offset,
    radius: Float,
    time: Float,
    phase: Float,
    scale: Float,
    alpha: Float
) {
    val t = time * 2 * PI.toFloat()
    val moveX = sin(t * 0.2f + phase) * 150f + cos(t * 0.1f) * 60f
    val moveY = cos(t * 0.15f + phase) * 120f + sin(t * 0.05f) * 50f
    val currentScale = scale * (0.9f + sin(t * 0.08f) * 0.2f)

    drawCircle(
        color = color.copy(alpha = 0.4f * alpha),
        radius = radius * currentScale,
        center = Offset(center.x + moveX, center.y + moveY),
        blendMode = BlendMode.Screen
    )
}
