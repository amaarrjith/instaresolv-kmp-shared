package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateInspectionDraftRequest(
    val id: Long = 0,
    val facilitiesId: Int?,
    val projectJson: String?,
    val inspectionTypeId: Int,
    val inspectionTypeName: String?,
    val inspectionTypeUpdatedTime: String?,
    val inspectionContentVersion: String?,
    val location: String?,
    val inspectionDateMillis: Long?,
    val description: String?,
    val notes: String?,
    val questionsJson: String?,
    val answersJson: String?,
    val imagesJson: String?,
    val createdAt: String?,
    val userId: Int
)
