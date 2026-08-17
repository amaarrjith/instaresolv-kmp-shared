package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AddInspectionRequest(
    val auditItemId: Int,
    val facilities: Int? = null,
    val inspectedBy: String,
    val location: String,
    val inspectionDate: String,
    val description: String,
    val subContractor: String? = null,
    val staticEquipment: List<InspectionStaticEquipment>,
    val notes: String,
    val images: List<InspectionImageRequest>
)

@Serializable
data class StaticEquipmentAnswer(
    val id: Int,
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
