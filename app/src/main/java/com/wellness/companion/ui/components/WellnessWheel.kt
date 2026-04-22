package com.wellness.companion.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wellness.companion.ui.theme.WellnessPalette
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun WellnessWheel(
    valence: Double, // -1.0 to 1.0
    arousal: Double, // -1.0 to 1.0
    onChange: (valence: Double, arousal: Double) -> Unit,
    modifier: Modifier = Modifier,
) {
    val measurer = rememberTextMeasurer()
    var size by remember { mutableStateOf(Size.Zero) }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(20.dp)
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    change.consume()
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val radius = min(size.width, size.height) / 2f
                    
                    val dx = change.position.x - center.x
                    val dy = change.position.y - center.y
                    val dist = min(sqrt(dx * dx + dy * dy), radius)
                    val angle = atan2(dy, dx)
                    
                    val newValence = cos(angle) * dist / radius
                    val newArousal = -sin(angle) * dist / radius // Invert Y
                    
                    onChange(newValence.toDouble(), newArousal.toDouble())
                }
            },
    ) {
        size = this.size
        val radius = min(size.width, size.height) / 2f
        val center = Offset(size.width / 2f, size.height / 2f)

        // 1. Background Glow
        drawCircle(
            brush = Brush.sweepGradient(
                colors = listOf(
                    WellnessPalette.SagePastel,
                    WellnessPalette.LavenderPastel,
                    WellnessPalette.RosePastel,
                    WellnessPalette.SagePastel
                ),
                center = center
            ),
            radius = radius,
            alpha = 0.4f
        )

        // 2. Glassy segments
        val sweep = 45f
        val colors = listOf(
            WellnessPalette.SagePastel, WellnessPalette.LavenderPastel, 
            WellnessPalette.RosePastel, WellnessPalette.SagePastel, 
            WellnessPalette.LavenderPastel, WellnessPalette.RosePastel, 
            WellnessPalette.LavenderPastel, WellnessPalette.SagePastel
        )
        
        colors.forEachIndexed { i, color ->
            drawArc(
                color = color.copy(alpha = 0.3f),
                startAngle = i * sweep - 90f,
                sweepAngle = sweep,
                useCenter = true,
                size = Size(radius * 2, radius * 2),
                topLeft = Offset(center.x - radius, center.y - radius)
            )
        }

        // 3. Elegant Grid
        for (r in listOf(0.25f, 0.5f, 0.75f, 1.0f)) {
            drawCircle(
                color = Color.White.copy(alpha = 0.05f),
                radius = radius * r,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )
        }

        // 4. Axis Labels
        val labelStyle = TextStyle(
            color = Color.White.copy(alpha = 0.3f),
            fontSize = 9.sp,
            fontWeight = FontWeight.Black
        )
        
        drawLabel(measurer, "ENERGISED", Offset(center.x, center.y - radius - 15.dp.toPx()), labelStyle)
        drawLabel(measurer, "CALM",      Offset(center.x, center.y + radius + 15.dp.toPx()), labelStyle)
        drawLabel(measurer, "LOW",       Offset(center.x - radius - 20.dp.toPx(), center.y), labelStyle)
        drawLabel(measurer, "HIGH",      Offset(center.x + radius + 20.dp.toPx(), center.y), labelStyle)

        // 5. The Orb (Indicator)
        val orbPos = Offset(
            center.x + (valence.toFloat() * radius),
            center.y - (arousal.toFloat() * radius)
        )
        
        drawCircle(
            color = Color.Black.copy(alpha = 0.2f),
            radius = 24.dp.toPx(),
            center = orbPos
        )
        
        drawCircle(
            color = WellnessPalette.Sage500,
            radius = 16.dp.toPx(),
            center = orbPos
        )
        
        drawCircle(
            color = Color.White,
            radius = 16.dp.toPx(),
            center = orbPos,
            style = Stroke(width = 3.dp.toPx())
        )
    }
}

private fun DrawScope.drawLabel(
    measurer: TextMeasurer,
    text: String,
    at: Offset,
    style: TextStyle,
) {
    val layout = measurer.measure(text, style)
    drawText(
        textLayoutResult = layout,
        topLeft = Offset(
            at.x - layout.size.width / 2f,
            at.y - layout.size.height / 2f,
        ),
    )
}
