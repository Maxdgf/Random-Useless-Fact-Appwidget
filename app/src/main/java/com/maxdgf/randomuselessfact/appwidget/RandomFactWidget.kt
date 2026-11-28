package com.maxdgf.randomuselessfact.appwidget


import android.content.Context

import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager

import com.maxdgf.randomuselessfact.appwidget.ui.FactLoadingErrorScreen
import com.maxdgf.randomuselessfact.appwidget.ui.FactLoadingScreen
import com.maxdgf.randomuselessfact.appwidget.ui.RandomUselessFactWidgetScreen


/** Glance appwidget action callback which updates
 * state of requesting random fact to `IDLE`. */
class SwitchToIdleStateAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        updateAppWidgetState(context, glanceId) { preferences ->
            preferences[FactRequestResult.state] = FactRequestState.IDLE.name // set IDLE state to preference
        }
        RandomFactWidget().update(context, glanceId)
    }
}

class RandomFactWidget : GlanceAppWidget() {
    /** Enqueues work of requesting random fact by API.
     * @param context glance appwidget context
     * @param id glance appwidget id */
    private fun enqueueFactDownloadWork(context: Context, id: GlanceId) {
        // get glance widget id int
        val widgetIdInt = GlanceAppWidgetManager(context).getAppWidgetId(id)

        // build work request
        val workerRequest = OneTimeWorkRequest.Builder(RandomFactWorker::class)
            .setInputData(
                Data.Builder()
                    .putInt("glance_appwidget_id", widgetIdInt) // put glance appwidget id int
                    .build()
            )
            .build()

        // enqueue work with unique name
        WorkManager.getInstance(context)
            .enqueueUniqueWork("glance_appwidget_work_$widgetIdInt", ExistingWorkPolicy.KEEP, workerRequest)
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val preferences = getAppWidgetState<Preferences>(context, id) // get appwidget preferences

        val currentFact = preferences[FactData.text] // get current fact's text
        val factSourceName = preferences[FactData.source] // get current fact's source name

        // check current fact's data
        // if current fact's data is not exists, request fact
        if (currentFact == null && factSourceName == null)
            enqueueFactDownloadWork(context, id)

        provideContent {
            val preferences = currentState<Preferences>()

            // get fact request state
            val factRequestState = preferences[FactRequestResult.state]?.let { state ->
                runCatching { FactRequestState.valueOf(state) }.getOrNull()
            } ?: FactRequestState.RUNNING

            GlanceTheme(colors = RandomFactWidgetTheme.colors) {
                Box(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .padding(all = 7.dp)
                        .background(colorProvider = GlanceTheme.colors.surface),
                    contentAlignment = Alignment.Center
                ) {
                    val currentFact = preferences[FactData.text] // current fact's text
                    val sourceName = preferences[FactData.source] // current fact's source name

                    // match screens with fact request state
                    when (factRequestState) {
                        FactRequestState.RUNNING -> FactLoadingScreen()

                        FactRequestState.SUCCESS, FactRequestState.IDLE -> {
                            RandomUselessFactWidgetScreen(
                                factText = currentFact ?: "???",
                                factSource = sourceName ?: "Fact not loaded, try refresh now.",
                                onRefreshFact = { enqueueFactDownloadWork(context, id) }
                            )
                        }

                        FactRequestState.FAILURE -> {
                            val errorMessage =
                                preferences[FactRequestResult.errorMessage] ?: "Unknown error."

                            FactLoadingErrorScreen(
                                message = errorMessage,
                                showLoadLastLoadedFactButton = currentFact != null && sourceName != null,
                                onShowLastLoadedFact = actionRunCallback<SwitchToIdleStateAction>(),
                                onRetry = { enqueueFactDownloadWork(context, id) }
                            )
                        }
                    }
                }
            }
        }
    }

    override suspend fun onDelete(context: Context, glanceId: GlanceId) {
        super.onDelete(context, glanceId)

        val workManager = WorkManager.getInstance(context) // get work manager instance
        val widgetIdInt = GlanceAppWidgetManager(context).getAppWidgetId(glanceId) // get Glance widget's id int
        val workName = "glance_appwidget_work_$widgetIdInt" // build widget's work unique name

        // check if the work is going
        if (!workManager.getWorkInfosForUniqueWork(workName).isDone)
            workManager.cancelUniqueWork(workName) // cancel work by unique name
    }
}