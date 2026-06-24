package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class GeneralContentsResponse(
    val content: String? = null
)
