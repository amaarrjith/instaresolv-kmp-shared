package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateViolationRequest(
    val facilitiesId: String? = null,
    val employeeName: String = "",
    val employeeId: String = "",
    val violationDate: String = "",
    val location: String? = null,
    val description: String? = null,
    val images: List<ImageDescriptionRequest>? = null,
    val reportedBy: String = "",
    val saveAsDraft: Boolean = false
)
