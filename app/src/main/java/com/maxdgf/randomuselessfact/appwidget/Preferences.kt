package com.maxdgf.randomuselessfact.appwidget

import androidx.datastore.preferences.core.stringPreferencesKey

object Fact {
    val text = stringPreferencesKey("current_useless_fact")
    val source = stringPreferencesKey("source_name")
}

object FactResult {
    val state = stringPreferencesKey("fact_work_state")
    val errorMessage = stringPreferencesKey("error_message")
}