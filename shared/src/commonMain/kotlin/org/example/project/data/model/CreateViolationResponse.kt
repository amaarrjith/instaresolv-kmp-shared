package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateViolationResponse(
    val violationId: Int? = null,
    val statusMessage: String? = null
)
