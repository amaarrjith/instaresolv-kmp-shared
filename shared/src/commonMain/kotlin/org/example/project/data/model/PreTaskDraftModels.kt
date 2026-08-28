package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CreatePreTaskDraftRequest(
    val id: Long = 0,
    val facilitiesId: Int?,
    val projectJson: String?,
    val dateMillis: Long?,
    val startTime: String?,
    val endTime: String?,
    val msraReference: String?,
    val permitReference: String?,
    val taskTitle: String?,
    val stepByStepAccount: String?,
    val contentsJson: String?,
    val questionsJson: String?,
    val questionAnswersJson: String?,
    val customQuestionsJson: String?,
    val attendeesJson: String?,
    val evidencesJson: String?,
    val selectedNotifyPersonJson: String?,
    val reportedBy: String?,
    val createdAt: String?,
    val userId: Int
)
