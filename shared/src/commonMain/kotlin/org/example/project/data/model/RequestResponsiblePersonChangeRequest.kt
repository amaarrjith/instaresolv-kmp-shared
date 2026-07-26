package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class RequestResponsiblePersonChangeRequest(
    val observationId: Int,
    val justification: String,
    val responsiblePerson: Int
)
