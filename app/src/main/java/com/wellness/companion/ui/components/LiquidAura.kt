package com.wellness.companion.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.wellness.companion.ui.theme.WellnessPalette
import java.io.File
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun LiquidAura(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val customImage = remember {
        val file = File(context.filesDir, "custom_background.jpg")
        if (file.exists()) BitmapFactory.decodeFile(file.absolutePath) else null
    }

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

    val breath by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breath"
    )

    Box(modifier = modifier.fillMaxSize()) {
        // 1. Deep Space Base
        Canvas(Modifier.fillMaxSize()) {
            drawRect(WellnessPalette.LiquidDeep)
        }

        // 2. The Memory Layer (User Photo)
        customImage?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(50.dp)
                    .alpha(0.15f + (breath * 0.05f))
                    .scale(1.1f + (breath * 0.05f))
            )
        }

        // 3. The "Breathing" Fluid Layer
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val globalScale = 1.0f + (breath * 0.15f)
            val globalAlpha = 0.6f + (breath * 0.4f)

            drawLiquidBlob(WellnessPalette.LiquidIndigo, Offset(w * 0.2f, h * 0.3f), w * 0.8f, time, 0f, globalScale, globalAlpha)
            drawLiquidBlob(WellnessPalette.LiquidTeal, Offset(w * 0.8f, h * 0.6f), w * 0.7f, time, 2.5f, globalScale, globalAlpha)
            drawLiquidBlob(WellnessPalette.LiquidRose, Offset(w * 0.3f, h * 0.9f), w * 0.75f, time, 4.2f, globalScale, globalAlpha)
        }
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
    val moveX = sin(t * 0.2f + phase) * 150f
    val moveY = cos(t * 0.15f + phase) * 120f
    val currentScale = scale * (0.9f + sin(t * 0.08f) * 0.2f)

    drawCircle(
        color = color.copy(alpha = 0.4f * alpha),
        radius = radius * currentScale,
        center = Offset(center.x + moveX, center.y + moveY),
        blendMode = BlendMode.Screen
    )
}
