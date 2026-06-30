package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class InspectionData(
    val id: Int? = null,
    val modelNumber: String? = null,
    val inspectedBy: String? = null,
    val location: String? = null,
    val inspectionDate: String? = null,
    val description: String? = null,
    val equipmentSource: String? = null,
    val subContractor: String? = null,
    val notes: String? = null,
    val createdAt: String? = null,
    val facilities: InspectionFacilities? = null,
    val staticEquipment: List<InspectionStaticEquipment>? = null,
    val images: List<InspectionImage>? = null,
    val auditItem: InspectionAuditItem? = null
)

@Serializable
data class InspectionFacilities(
    val groupId: String? = null,
    val groupName: String? = null,
    val groupCode: String? = null,
    val groupImage: String? = null
)

@Serializable
data class InspectionStaticEquipment(
    val id: Int? = null,
    val title: String? = null,
    val selectedValue: Int? = null
)

@Serializable
data class InspectionImage(
    val image: String? = null,
    val description: String? = null
)

@Serializable
data class InspectionAuditItem(
    val auditItemId: Int? = null,
    val auditItemTitle: String? = null
)
