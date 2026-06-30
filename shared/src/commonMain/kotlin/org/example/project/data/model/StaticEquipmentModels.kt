package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class StaticEquipmentListRequest(
    val id: Int
)

@Serializable
data class StaticEquipmentListResponse(
    val updatedTime: kotlinx.serialization.json.JsonElement? = null,
    val contents: List<StaticEquipmentContent> = emptyList()
)

@Serializable
data class StaticEquipmentContent(
    val id: Int,
    val title: String
)
