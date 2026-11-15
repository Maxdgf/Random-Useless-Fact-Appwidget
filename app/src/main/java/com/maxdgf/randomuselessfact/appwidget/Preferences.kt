package com.maxdgf.randomuselessfact.appwidget

import androidx.datastore.preferences.core.stringPreferencesKey

object FactData {
    val text = stringPreferencesKey("fact_text")
    val source = stringPreferencesKey("fact_source_name")
}

object FactRequestResult {
    val state = stringPreferencesKey("fact_request_state")
    val errorMessage = stringPreferencesKey("fact_request_error_message")
}