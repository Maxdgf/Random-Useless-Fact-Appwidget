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


class RandomUselessFactWorker(
    private val context: Context,
    private val params: WorkerParameters
) : CoroutineWorker(context, params) {
    private suspend fun updateFactAppWidgetState(
        context: Context,
        glanceId: GlanceId,
        updateState: suspend (MutablePreferences) -> Unit
    ) {
        updateAppWidgetState(context, glanceId, updateState)
        RandomUselessFactWidget().update(context, glanceId)
    }

    override suspend fun doWork(): Result {
        val widgetId = params.inputData.getInt("glance_appwidget_id", 0)
        val glanceWidgetId = GlanceAppWidgetManager(context).getGlanceIdBy(widgetId)

        return try {
            updateFactAppWidgetState(context, glanceWidgetId) { preferences ->
                preferences[FactResult.state] = FactWorkState.RUNNING.name
            }

            val randomUselessFact = withContext(Dispatchers.IO) {
                return@withContext httpClient.get{}.body<UselessFact>()
            }

            updateFactAppWidgetState(context, glanceWidgetId) { preferences ->
                preferences[Fact.text] = randomUselessFact.text
                preferences[Fact.source] = randomUselessFact.source

                preferences[FactResult.state] = FactWorkState.SUCCESS.name
                preferences[FactResult.errorMessage] = ""
            }

            Result.success()
        } catch (e: IOException) {
            if (BuildConfig.DEBUG)
                e.printStackTrace()

            updateFactAppWidgetState(context, glanceWidgetId) { preferences ->
                preferences[FactResult.state] = FactWorkState.FAILURE.name
                preferences[FactResult.errorMessage] = e.message ?: ""
            }

            Result.failure()
        }
    }
}