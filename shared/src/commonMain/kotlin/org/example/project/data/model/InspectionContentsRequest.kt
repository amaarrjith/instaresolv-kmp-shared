package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class InspectionContentsRequest(
    val updatedTime: String
)

@Serializable
data class InspectionContentsResponse(
    val contentsList: List<InspectionContent>
)

@Serializable
data class InspectionContent(
    val version: String? = null,
    val type: Int? = null,
    val contents: List<InspectionStaticEquipment>
)