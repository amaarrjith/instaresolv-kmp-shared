package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class RequestDeleteObservationRequest(
    val observationId: Int,
    val justification: String
)
