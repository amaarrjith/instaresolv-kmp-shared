package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ViolationData(
    val id: Int,
    val reportedBy: String? = null,
    val employeeName: String? = null,
    val employeeId: String? = null,
    val violationDate: String? = null,
    val location: String? = null,
    val description: String? = null,
    val translatedDescription: String? = null,
    val createdAt: String? = null,
    val facilities: ViolationFacility? = null,
    val images: List<ViolationImage>? = null
)

@Serializable
data class ViolationFacility(
    val groupId: String? = null,
    val groupName: String? = null,
    val groupCode: String? = null,
    val groupImage: String? = null
)

@Serializable
data class ViolationImage(
    val image: String? = null,
    val description: String? = null
)
