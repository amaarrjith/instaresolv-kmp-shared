package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class GenerateIncidentPdfRequest(
    val incidentId: Int
)

@Serializable
data class GenerateObservationPdfRequest(
    val observationId: Int
)

@Serializable
data class GenerateViolationPdfRequest(
    val violationId: Int
)
