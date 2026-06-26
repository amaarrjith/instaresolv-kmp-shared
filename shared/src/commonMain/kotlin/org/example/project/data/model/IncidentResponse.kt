package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class IncidentData(
    val id: Int,
    val reportedBy: String? = null,
    val incidentDate: String? = null,
    val incidentTime: String? = null,
    val incidentLocation: String? = null,
    val incidentType: List<Int>? = null,
    val injuredEmployees: List<InjuredEmployee>? = null,
    val description: String? = null,
    val corrections: String? = null,
    val createdAt: String? = null,
    val facilities: IncidentFacility? = null,
    val images: List<IncidentImage>? = null
)

@Serializable
data class InjuredEmployee(
    val id: String? = null,
    val employeeCode: String? = null,
    val employeeName: String? = null,
    val companyName: String? = null,
    val profession: String? = null
)

@Serializable
data class IncidentFacility(
    val groupId: String? = null,
    val groupName: String? = null,
    val groupCode: String? = null,
    val groupImage: String? = null
)

@Serializable
data class IncidentImage(
    val image: String? = null,
    val description: String? = null
)


