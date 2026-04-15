package com.wellness.companion.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * "Wellness Wheel" — an interactive Canvas control that captures both the
 * [valence] (angle around the wheel) and [arousal] (radial distance from the
 * centre). Tapping or dragging snaps the indicator smoothly.
 *
 * Implementation notes for scale:
 *  - One Canvas + one pointerInput. No bitmaps, no vectors.
 *  - No per-frame allocations inside `onDraw`; arcs/rings go through
 *    drawArc / drawCircle which the runtime reuses internally.
 *  - Labels go through a shared [TextMeasurer] (cached via
 *    [rememberTextMeasurer]) — creating measurers on every draw is a top
 *    cause of Canvas jank in Compose.
 *
 *   valence range: -2 (low / left) .. +2 (high / right)
 *   arousal range:  0 (calm / centre)  .. 4 (energised / outer)
 */
@Composable
fun WellnessWheel(
    valence: Int,
    arousal: Int,
    onChange: (valence: Int, arousal: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    val measurer = rememberTextMeasurer(cacheSize = 16)

    val scope = rememberCoroutineScope()
    val indicator = remember { Animatable(Offset(0f, 0f), OffsetConverter) }
    var size by remember { mutableStateOf(Size.Zero) }

    LaunchedEffect(valence, arousal, size) {
        if (size == Size.Zero) return@LaunchedEffect
        indicator.animateTo(
            targetValue = toOffset(valence, arousal, size),
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow,
            ),
        )
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val clamped = clampToDisc(offset, size)
                    emit(clamped, size, onChange)
                    scope.launch {
                        indicator.animateTo(
                            clamped,
                            spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        )
                    }
                }
            }
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    change.consume()
                    val clamped = clampToDisc(change.position, size)
                    emit(clamped, size, onChange)
                    scope.launch { indicator.snapTo(clamped) }
                }
            },
    ) {
        size = this.size
        val radius = min(size.width, size.height) / 2f
        val center = Offset(size.width / 2f, size.height / 2f)

        // Petals – 8 radial segments.
        val petals = listOf(
            scheme.primaryContainer, scheme.secondaryContainer, scheme.tertiaryContainer,
            scheme.primary.copy(alpha = 0.25f), scheme.secondary.copy(alpha = 0.25f),
            scheme.tertiary.copy(alpha = 0.25f), scheme.primaryContainer.copy(alpha = 0.6f),
            scheme.tertiaryContainer.copy(alpha = 0.6f),
        )
        val sweep = 360f / petals.size
        petals.forEachIndexed { i, color ->
            drawArc(
                color = color,
                startAngle = -90f + i * sweep,
                sweepAngle = sweep - 1.5f,
                useCenter = true,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
            )
        }

        // Concentric arousal rings.
        val ringColor = scheme.onSurface.copy(alpha = 0.08f)
        for (ring in 1..4) {
            drawCircle(
                color = ringColor,
                radius = radius * (ring / 4f),
                center = center,
                style = Stroke(width = 1.dp.toPx()),
            )
        }

        // Centre well – calm pool.
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(scheme.surface, scheme.surface.copy(alpha = 0f)),
                center = center,
                radius = radius * 0.35f,
            ),
            radius = radius * 0.35f,
            center = center,
        )

        val labelStyle = TextStyle(color = scheme.onSurfaceVariant, fontSize = 11.sp)
        drawLabel(measurer, "high",   Offset(center.x, center.y - radius + 14.dp.toPx()), labelStyle)
        drawLabel(measurer, "low",    Offset(center.x, center.y + radius - 22.dp.toPx()), labelStyle)
        drawLabel(measurer, "calm",   Offset(center.x - radius + 20.dp.toPx(), center.y), labelStyle)
        drawLabel(measurer, "lively", Offset(center.x + radius - 26.dp.toPx(), center.y), labelStyle)

        val pos = clampToDisc(indicator.value, size)
        drawCircle(color = scheme.onSurface.copy(alpha = 0.10f), radius = 18.dp.toPx(), center = pos)
        drawCircle(color = scheme.primary, radius = 12.dp.toPx(), center = pos)
        drawCircle(
            color = Color.White,
            radius = 12.dp.toPx(),
            center = pos,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round),
        )
    }
}

// ---------------------------------------------------------------------------
// Pure geometry helpers — unit-testable, no Compose dependencies beyond Offset.
// ---------------------------------------------------------------------------

private fun clampToDisc(pt: Offset, size: Size): Offset {
    if (size == Size.Zero) return pt
    val radius = min(size.width, size.height) / 2f
    val center = Offset(size.width / 2f, size.height / 2f)
    val dx = pt.x - center.x
    val dy = pt.y - center.y
    val d = sqrt(dx * dx + dy * dy)
    return if (d <= radius) pt else Offset(center.x + dx / d * radius, center.y + dy / d * radius)
}

private fun emit(pt: Offset, size: Size, onChange: (Int, Int) -> Unit) {
    if (size == Size.Zero) return
    val (v, a) = fromOffset(pt, size)
    onChange(v, a)
}

private fun toOffset(valence: Int, arousal: Int, size: Size): Offset {
    val center = Offset(size.width / 2f, size.height / 2f)
    val radius = min(size.width, size.height) / 2f
    val angle = (valence / 2f) * (PI / 2).toFloat()
    val r = (arousal / 4f) * radius
    return Offset(center.x + cos(angle) * r, center.y - sin(angle) * r)
}

private fun fromOffset(pt: Offset, size: Size): Pair<Int, Int> {
    val center = Offset(size.width / 2f, size.height / 2f)
    val radius = min(size.width, size.height) / 2f
    val dx = pt.x - center.x
    val dy = -(pt.y - center.y)
    val distance = sqrt(dx * dx + dy * dy).coerceAtMost(radius)
    val arousal = (distance / radius * 4f).toInt().coerceIn(0, 4)
    val angle = atan2(dy, dx)
    val valence = (cos(angle) * 2f).toInt().coerceIn(-2, 2)
    return valence to arousal
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

private val OffsetConverter: TwoWayConverter<Offset, AnimationVector2D> =
    TwoWayConverter(
        convertToVector = { AnimationVector2D(it.x, it.y) },
        convertFromVector = { Offset(it.v1, it.v2) },
    )
