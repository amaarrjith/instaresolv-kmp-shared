package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AddIncidentResponse(
    val hasError: Boolean? = null,
    val errorCode: Int? = null,
    val message: String? = null,
    val response: AddIncidentData? = null
)

@Serializable
data class AddIncidentData(
    val incidentId: Int? = null,
    val statusMessage: String? = null
)
