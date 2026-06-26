package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CloseObservationRequest(
    val observationId: Int,
    val description: String,
    val imageDescription: List<ObservationImageDescription>
)
