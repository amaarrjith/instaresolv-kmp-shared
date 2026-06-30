package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AddInspectionRequest(
    val auditItemId: Int,
    val facilities: Int? = null,
    val modelNumber: String,
    val inspectedBy: String,
    val location: String,
    val inspectionDate: String,
    val description: String,
    val equipmentSource: Int,
    val subContractor: String? = null,
    val staticEquipment: List<StaticEquipmentAnswer>,
    val notes: String,
    val images: List<InspectionImageRequest>
)

@Serializable
data class StaticEquipmentAnswer(
    val equipmentId: Int,
    val selectedValue: Int
)

@Serializable
data class InspectionImageRequest(
    val image: String,
    val description: String,
    val isAiGeneratedDescription: Boolean = false
)

@Serializable
data class AddInspectionResponse(
    val inspectionId: Int,
    val statusMessage: String
)
