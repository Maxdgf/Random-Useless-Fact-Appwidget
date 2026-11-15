package com.maxdgf.randomuselessfact.appwidget


import android.content.Context

import androidx.datastore.preferences.core.MutablePreferences
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

import io.ktor.client.call.body
import io.ktor.client.request.get

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.IOException

import com.maxdgf.randomuselessfact.BuildConfig
import com.maxdgf.randomuselessfact.model.UselessFact
import com.maxdgf.randomuselessfact.network.httpClient


class RandomFactWorker(
    private val context: Context,
    private val params: WorkerParameters
) : CoroutineWorker(context, params) {
    /** Updates fact appwidget state.
     *
     * @param context glance appwidget context
     * @param glanceId glance appwidget id
     * @param updateState update preferences action */
    private suspend fun updateFactAppWidgetState(
        context: Context,
        glanceId: GlanceId,
        updateState: suspend (MutablePreferences) -> Unit
    ) {
        updateAppWidgetState(context, glanceId, updateState)
        RandomFactWidget().update(context, glanceId)
    }

    override suspend fun doWork(): Result {
        val widgetId = params.inputData.getInt("glance_appwidget_id", 0) // get glance appwidget id int from params
        val glanceId = GlanceAppWidgetManager(context).getGlanceIdBy(widgetId) // get glance appwidget id

        return try {
            // update fact appwidget state
            updateFactAppWidgetState(context, glanceId) { preferences ->
                preferences[FactRequestResult.state] = FactRequestState.RUNNING.name
            }

            // get random fact by API
            val randomFact = withContext(Dispatchers.IO) {
                return@withContext httpClient.get{}.body<UselessFact>()
            }

            // update fact appwidget state
            updateFactAppWidgetState(context, glanceId) { preferences ->
                // update current fact's data
                preferences[FactData.text] = randomFact.text // set text
                preferences[FactData.source] = randomFact.source // set source name

                // update fact request result data
                preferences[FactRequestResult.state] = FactRequestState.SUCCESS.name // set 'SUCCESS' state
                preferences[FactRequestResult.errorMessage] = "" // clear error message
            }

            Result.success()
        } catch (ioe: IOException) {
            if (BuildConfig.DEBUG)
                ioe.printStackTrace() // print exception stack trace

            // update fact appwidget state
            updateFactAppWidgetState(context, glanceId) { preferences ->
                // update fact request result data
                preferences[FactRequestResult.state] = FactRequestState.FAILURE.name // set 'FAILURE' state
                preferences[FactRequestResult.errorMessage] = ioe.message ?: "" // set error message (if exists)
            }

            Result.failure()
        }
    }
}