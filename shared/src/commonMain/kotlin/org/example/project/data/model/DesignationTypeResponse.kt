package org.example.project.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DesignationTypeResponse(
    @SerialName("id") val id: Int,
    @SerialName("designation") val designation: String
)
