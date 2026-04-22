package com.wellness.companion.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

private val LiquidScheme = darkColorScheme(
    primary        = WellnessPalette.Sage500,
    onPrimary      = WellnessPalette.LiquidDeep,
    primaryContainer = WellnessPalette.SagePastel,
    onPrimaryContainer = WellnessPalette.Sage500,
    secondary      = WellnessPalette.Teal300,
    background     = WellnessPalette.LiquidDeep,
    onBackground   = WellnessPalette.TextPrimary,
    surface        = WellnessPalette.GlassSurface,
    onSurface      = WellnessPalette.TextPrimary,
    surfaceVariant = WellnessPalette.LiquidIndigo,
    onSurfaceVariant = WellnessPalette.TextSecondary,
)

private val WellnessShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small      = RoundedCornerShape(12.dp),
    medium     = RoundedCornerShape(24.dp),
    large      = RoundedCornerShape(32.dp),
    extraLarge = RoundedCornerShape(40.dp),
)

@Composable
fun WellnessTheme(
    content: @Composable () -> Unit,
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = LiquidScheme.background.toArgb()
            window.navigationBarColor = LiquidScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = LiquidScheme,
        typography  = WellnessTypography,
        shapes      = WellnessShapes,
        content     = content,
    )
}
