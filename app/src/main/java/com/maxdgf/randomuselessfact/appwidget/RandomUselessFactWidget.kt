package com.maxdgf.randomuselessfact.appwidget

import android.content.Context
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager

class SwitchToIdleStateAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        updateAppWidgetState(context, glanceId) { preferences ->
            preferences[FactResult.state] = FactWorkState.IDLE.name
        }
        RandomUselessFactWidget().update(context, glanceId)
    }
}

class RandomUselessFactWidget : GlanceAppWidget() {
    private fun enqueueFactDownloadWork(context: Context, id: Int) {
        val workerRequest = OneTimeWorkRequest.Builder(RandomUselessFactWorker::class)
            .setInputData(
                Data.Builder()
                    .putInt("glance_appwidget_id", id)
                    .build()
            )
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork("glance_appwidget_work_$id", ExistingWorkPolicy.KEEP, workerRequest)
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val preferences = getAppWidgetState<Preferences>(context, id)

        val currentUselessFact = preferences[Fact.text]
        val factSourceName = preferences[Fact.source]

        if (currentUselessFact == null && factSourceName == null) {
            val widgetId = GlanceAppWidgetManager(context).getAppWidgetId(id)
            enqueueFactDownloadWork(context, widgetId)
        }

        provideContent {
            val preferences = currentState<Preferences>()

            val factWorkState = preferences[FactResult.state]?.let { state ->
                runCatching { FactWorkState.valueOf(state) }.getOrNull()
            } ?: FactWorkState.RUNNING

            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .padding(all = 7.dp)
                    .background(colorProvider = GlanceTheme.colors.surface),
                contentAlignment = Alignment.Center
            ) {
                val currentUselessFact = preferences[Fact.text]
                val sourceName = preferences[Fact.source]

                when (factWorkState) {
                    FactWorkState.RUNNING -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = GlanceTheme.colors.primary)
                            Text(
                                text = "Loading useful fact...",
                                style = TextStyle(color = GlanceTheme.colors.onSurface)
                            )
                        }
                    }

                    FactWorkState.SUCCESS, FactWorkState.IDLE -> {
                        RandomUselessFactWidgetScreen(
                            factText = currentUselessFact ?: "???",
                            factSource = sourceName ?: "Fact not loaded, try refresh now.",
                            onRefreshFact = {
                                val widgetId = GlanceAppWidgetManager(context).getAppWidgetId(id)
                                enqueueFactDownloadWork(context, widgetId)
                            }
                        )
                    }

                    FactWorkState.FAILURE -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = GlanceModifier
                                    .padding(all = 5.dp)
                                    .background(colorProvider = GlanceTheme.colors.error)
                                    .cornerRadius(radius = 15.dp)
                            ) {
                                val errorMessage = preferences[FactResult.errorMessage] ?: "Unknown error."
                                Text(
                                    text = "Fact not uploaded, please retry or load latest fact!\nError: $errorMessage",
                                    style = TextStyle(
                                        color = GlanceTheme.colors.onError,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 12.sp
                                    )
                                )
                            }

                            Spacer(modifier = GlanceModifier.height(10.dp))

                            if (currentUselessFact != null && sourceName != null) {
                                Button(
                                    text = "last fact",
                                    onClick = actionRunCallback<SwitchToIdleStateAction>()
                                )

                                Spacer(modifier = GlanceModifier.height(5.dp))
                            }

                            Button(
                                text = "retry",
                                onClick = {
                                    val widgetId = GlanceAppWidgetManager(context).getAppWidgetId(id)
                                    enqueueFactDownloadWork(context, widgetId)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}