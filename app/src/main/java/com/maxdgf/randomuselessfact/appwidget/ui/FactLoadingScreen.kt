package com.maxdgf.randomuselessfact.appwidget.ui

import androidx.compose.runtime.Composable
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.semantics.semantics
import androidx.glance.semantics.testTag
import androidx.glance.text.Text
import androidx.glance.text.TextStyle

/** Creates fact loading screen. */
@Composable
fun FactLoadingScreen() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator(color = GlanceTheme.colors.primary)
        Text(
            text = "Loading useful fact...",
            modifier = GlanceModifier.semantics { testTag = "loading_text" },
            style = TextStyle(color = GlanceTheme.colors.onSurface)
        )
    }
}