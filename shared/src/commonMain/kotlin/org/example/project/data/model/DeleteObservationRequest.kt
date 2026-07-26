package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DeleteObservationRequest(
    val observationId: Int
)

