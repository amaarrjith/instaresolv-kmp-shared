package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class InspectionDetailRequest(
    val id: Int
)

@Serializable
data class InspectionDetailResponse(
    val id: Int,
    val modelNumber: String? = null,
    val inspectedBy: String? = null,
    val location: String? = null,
    val inspectionDate: String? = null,
    val description: String? = null,
    val translatedDescription: String? = null,
    val equipmentSource: String? = null,
    val subContractor: String? = null,
    val notes: String? = null,
    val translatedNotes: String? = null,
    val createdAt: String? = null,
    val facilities: InspectionFacility? = null,
    val staticEquipment: List<InspectionStaticEquipment>? = null,
    val images: List<InspectionImageDetail>? = null,
    val auditItem: InspectionAuditItem? = null
)

@Serializable
data class InspectionFacility(
    val groupId: String? = null,
    val groupName: String? = null,
    val groupCode: String? = null,
    val groupImage: String? = null
)

@Serializable
data class InspectionImageDetail(
    val image: String? = null,
    val description: String? = null
)
