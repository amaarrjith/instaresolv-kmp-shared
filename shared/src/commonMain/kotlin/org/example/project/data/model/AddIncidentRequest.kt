package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AddIncidentRequest(
    val corrections: String = "",
    val facilitiesId: String = "",
    val incidentType: List<Int> = emptyList(),
    val injuredEmployees: List<InjuredEmployeeRequest> = emptyList(),
    val incidentTime: String = "",
    val createdAt: String = "",
    val incidentLocation: String = "",
    val reportedBy: String = "",
    val incidentDate: String = "",
    val saveAsDraft: Boolean = false,
    val images: List<ImageDescriptionRequest> = emptyList(),
    val description: String = ""
)

@Serializable
data class InjuredEmployeeRequest(
    val profession: String = "",
    val id: Int = -1,
    val employeeName: String = "",
    val companyName: String = "",
    val employeeCode: String = ""
)

@Serializable
data class CreateIncidentDraftRequest(
    val id: Long = 0,
    val facilitiesId: Int?,
    val projectJson: String?,
    val reportedBy: String?,
    val incidentDateMillis: Long?,
    val incidentTime: String?,
    val incidentLocation: String?,
    val incidentTypesJson: String?,
    val hasInjuredPerson: Boolean?,
    val injuredEmployeesJson: String?,
    val description: String?,
    val corrections: String?,
    val imagesJson: String?,
    val createdAt: String?,
    val userId: Int
)
