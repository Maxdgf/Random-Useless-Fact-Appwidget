package com.maxdgf.randomuselessfact.appwidget.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.Action
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle

/** Creates the fact loading error screen.
 *
 * @param message error message text
 * @param showLoadLastLoadedFactButton if false, the show load last loaded button will not be shown
 * @param onShowLastLoadedFact the action to be performed when the load last loaded button is clicked
 * @param onRetry will be called when the retry button is clicked */
@Composable
fun FactLoadingErrorScreen(
    message: String,
    showLoadLastLoadedFactButton: Boolean,
    onShowLastLoadedFact: Action,
    onRetry: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Error message text field
        Box(
            modifier = GlanceModifier
                .padding(all = 5.dp)
                .background(colorProvider = GlanceTheme.colors.error)
                .cornerRadius(radius = 15.dp)
        ) {
            Text(
                text = "FactData not uploaded, please retry or load latest fact!\nError: $message",
                style = TextStyle(
                    color = GlanceTheme.colors.onError,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp
                )
            )
        }

        Spacer(modifier = GlanceModifier.height(10.dp))

        // Load last loaded fact button
        if (showLoadLastLoadedFactButton) {
            Button(
                text = "last fact",
                onClick = onShowLastLoadedFact
            )

            Spacer(modifier = GlanceModifier.height(5.dp))
        }

        // Retry button
        Button(
            text = "retry",
            onClick = onRetry
        )
    }
}