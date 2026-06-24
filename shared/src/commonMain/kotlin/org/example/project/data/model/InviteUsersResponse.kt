package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CommonModelResponse(
    val isSuccess: Boolean? = false,
    val statusMessage: String? = ""
)