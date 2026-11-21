package com.maxdgf.randomuselessfact.appwidget

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.glance.material3.ColorProviders

object RandomFactWidgetTheme {
    /** Light and Dark theme colors. */
    val colors = ColorProviders(
        light = lightColorScheme(
            primary = Color(0xFF00AAD4),
            onPrimary = Color(0xFFE8E5E5),
            surface = Color(0xFFEDE7E7),
            surfaceVariant = Color(0xFFCAC8C8),
            onSurface = Color(0xFF232323)
        ),
        dark = darkColorScheme(
            primary = Color(0xFF0088C7),
            onPrimary = Color(0xFF1B1B1B),
            surface = Color(0xFF212121),
            surfaceVariant = Color(0xFF575555),
            onSurface = Color(0xFFF0F0F0)
        )
    )
}