package com.maxdgf.randomuselessfact.model

import kotlinx.serialization.Serializable

@Serializable
data class UselessFact(
    val text: String,
    val source: String
)