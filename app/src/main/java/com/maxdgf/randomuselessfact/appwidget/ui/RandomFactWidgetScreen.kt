package com.maxdgf.randomuselessfact.appwidget.ui


import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.components.CircleIconButton
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle

import com.maxdgf.randomuselessfact.R


/** Creates screen for random useless fact appwidget.
 *
 * @param factText fact's text
 * @param factSource fact's source name
 * @param onRefreshFact will be called when user clicks on refresh fact button */
@Composable
fun RandomUselessFactWidgetScreen(
    factText: String,
    factSource: String,
    onRefreshFact: () -> Unit
) {
    Column(modifier = GlanceModifier.fillMaxSize()) {
        // Topbar
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                provider = ImageProvider(R.drawable.outline_lightbulb_24),
                contentDescription = "Lightbulb icon",
                modifier = GlanceModifier.size(30.dp),
                colorFilter = ColorFilter.tint(colorProvider = GlanceTheme.colors.primary)
            )

            // FactData source name text
            Text(
                text = factSource,
                modifier = GlanceModifier
                    .padding(all = 5.dp)
                    .background(colorProvider = GlanceTheme.colors.primary)
                    .cornerRadius(radius = 18.dp),
                style = TextStyle(
                    color = GlanceTheme.colors.onPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            )

            // Colored spacer line
            Spacer(modifier = GlanceModifier.width(2.dp))
            Spacer(
                modifier = GlanceModifier
                    .height(2.dp)
                    .defaultWeight()
                    .background(colorProvider = GlanceTheme.colors.primary)
                    .cornerRadius(radius = 2.dp)
            )
            Spacer(modifier = GlanceModifier.width(2.dp))

            // Refresh fact button
            CircleIconButton(
                onClick = onRefreshFact,
                modifier = GlanceModifier.size(35.dp),
                imageProvider = ImageProvider(R.drawable.baseline_refresh_24),
                contentDescription = "Refresh useless fact button",
                backgroundColor = GlanceTheme.colors.primary,
                contentColor = GlanceTheme.colors.onPrimary
            )
        }

        Spacer(modifier = GlanceModifier.height(5.dp))

        // FactData text field
        Box(
            modifier = GlanceModifier
                .fillMaxWidth()
                .defaultWeight()
                .padding(all = 5.dp)
                .background(colorProvider = GlanceTheme.colors.surfaceVariant)
                .cornerRadius(radius = 15.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "💡 $factText", // lightbulb emoji for style :)
                style = TextStyle(
                    color = GlanceTheme.colors.onSurface,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp
                )
            )
        }
    }
}