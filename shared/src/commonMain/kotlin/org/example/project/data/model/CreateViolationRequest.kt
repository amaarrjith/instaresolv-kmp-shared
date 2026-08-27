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

@Serializable
data class LocalViolationImage(
    val id: Int,
    val imageUrl: String? = null,
    val description: String = ""
)

@Serializable
data class CreateViolationDraftRequest(
    val draftId: Long? = null,
    val userId: Int,
    val facilitiesId: String? = null,
    val facility: Project? = null,
    val employeeName: String = "",
    val employeeId: String = "",
    val violationDate: String = "",
    val violationDateMillis: Long? = null,
    val location: String? = null,
    val description: String? = null,
    val images: List<LocalViolationImage> = emptyList(),
    val reportedBy: String = "",
    val createdAt: String? = null
)
