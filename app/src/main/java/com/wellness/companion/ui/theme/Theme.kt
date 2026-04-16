package com.wellness.companion.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

private val LightScheme = lightColorScheme(
    primary        = WellnessPalette.Sage500,
    onPrimary      = WellnessPalette.Cream,
    primaryContainer = WellnessPalette.Sage100,
    onPrimaryContainer = WellnessPalette.Sage700,
    secondary      = WellnessPalette.Rose500,
    onSecondary    = WellnessPalette.Cream,
    secondaryContainer = WellnessPalette.Rose100,
    onSecondaryContainer = WellnessPalette.Ink,
    tertiary       = WellnessPalette.Lavender500,
    tertiaryContainer = WellnessPalette.Lavender100,
    onTertiaryContainer = WellnessPalette.Ink,
    background     = WellnessPalette.Cream,
    onBackground   = WellnessPalette.Ink,
    surface        = WellnessPalette.Surface,
    onSurface      = WellnessPalette.Ink,
    surfaceVariant = WellnessPalette.SurfaceDim,
    onSurfaceVariant = WellnessPalette.InkMuted,
)

private val DarkScheme = darkColorScheme(
    primary        = WellnessPalette.Sage300,
    onPrimary      = WellnessPalette.SurfaceDark,
    primaryContainer = WellnessPalette.Sage700,
    onPrimaryContainer = WellnessPalette.Sage50,
    secondary      = WellnessPalette.Rose300,
    onSecondary    = WellnessPalette.SurfaceDark,
    tertiary       = WellnessPalette.Lavender300,
    background     = WellnessPalette.SurfaceDark,
    onBackground   = WellnessPalette.OnDark,
    surface        = WellnessPalette.SurfaceDark,
    onSurface      = WellnessPalette.OnDark,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = WellnessPalette.OnDark,
)

private val DarkSurfaceVariant = Color(0xFF24272A)

private val WellnessShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small      = RoundedCornerShape(10.dp),
    medium     = RoundedCornerShape(18.dp),
    large      = RoundedCornerShape(26.dp),
    extraLarge = RoundedCornerShape(36.dp),
)

@Composable
fun WellnessTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val scheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val ctx = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
        }
        darkTheme -> DarkScheme
        else      -> LightScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = scheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = scheme,
        typography  = WellnessTypography,
        shapes      = WellnessShapes,
        content     = content,
    )
}
