package com.wellness.companion.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Soft, organic pastel palette. The system picks Material-3 roles from these.
 * Values were picked to maintain WCAG AA contrast on both light and dark
 * surfaces (verified at contrast checker with on-surface pairs).
 */
object WellnessPalette {

    // Primary — muted sage
    val Sage50  = Color(0xFFEFF5EF)
    val Sage100 = Color(0xFFD7E6D8)
    val Sage300 = Color(0xFF9DBFA0)
    val Sage500 = Color(0xFF6F9A74)
    val Sage700 = Color(0xFF4C7451)

    // Secondary — rose blush
    val Rose100 = Color(0xFFFAE2E2)
    val Rose300 = Color(0xFFE8A9A9)
    val Rose500 = Color(0xFFC97A7A)

    // Tertiary — powder lavender
    val Lavender100 = Color(0xFFE9E5F5)
    val Lavender300 = Color(0xFFB7AEDB)
    val Lavender500 = Color(0xFF8676B4)

    // Neutrals
    val Cream       = Color(0xFFFBF8F3)
    val Ink         = Color(0xFF23272A)
    val InkMuted    = Color(0xFF6A6F73)
    val Surface     = Color(0xFFFFFFFF)
    val SurfaceDim  = Color(0xFFF3EEE7)
    val SurfaceDark = Color(0xFF1A1C1E)
    val OnDark      = Color(0xFFE9EAEC)
}
