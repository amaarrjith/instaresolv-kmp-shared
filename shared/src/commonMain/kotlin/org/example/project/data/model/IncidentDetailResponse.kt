package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class IncidentDetailResponse(
    val id: Int? = null,
    val reportedBy: String? = null,
    val incidentDate: String? = null,
    val incidentTime: String? = null,
    val incidentLocation: String? = null,
    val incidentType: List<Int>? = null,
    val injuredEmployees: List<IncidentDetailInjuredEmployee>? = null,
    val description: String? = null,
    val translatedDescription: String? = null,
    val corrections: String? = null,
    val translatedCorrections: String? = null,
    val createdAt: String? = null,
    val facilities: IncidentDetailFacilities? = null,
    val images: List<IncidentDetailImage>? = null
)

@Serializable
data class IncidentDetailInjuredEmployee(
    val id: String? = null,
    val employeeCode: String? = null,
    val employeeName: String? = null,
    val companyName: String? = null,
    val profession: String? = null
)

@Serializable
data class IncidentDetailFacilities(
    val groupId: String? = null,
    val groupName: String? = null,
    val groupCode: String? = null,
    val groupImage: String? = null
)

@Serializable
data class IncidentDetailImage(
    val image: String? = null,
    val description: String? = null
)
