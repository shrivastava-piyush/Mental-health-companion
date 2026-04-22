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

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Base background
        drawRect(WellnessPalette.LiquidDeep)

        // Drifting liquid blobs
        drawLiquidBlob(WellnessPalette.LiquidIndigo, Offset(w * 0.2f, h * 0.3f), 500f, time, 0f)
        drawLiquidBlob(WellnessPalette.LiquidTeal, Offset(w * 0.8f, h * 0.6f), 450f, time, 2f)
        drawLiquidBlob(WellnessPalette.LiquidRose, Offset(w * 0.3f, h * 0.9f), 400f, time, 4f)
        drawLiquidBlob(WellnessPalette.LiquidAmber, Offset(w * 0.7f, h * 0.1f), 350f, time, 1f)
    }
}

private fun DrawScope.drawLiquidBlob(
    color: Color,
    center: Offset,
    radius: Float,
    time: Float,
    phase: Float
) {
    val moveX = sin(time * 2 * Math.PI.toFloat() + phase) * 60f
    val moveY = cos(time * 2 * Math.PI.toFloat() * 0.7f + phase) * 40f

    drawCircle(
        color = color.copy(alpha = 0.4f),
        radius = radius,
        center = Offset(center.x + moveX, center.y + moveY),
        blendMode = BlendMode.Screen
    )
}
