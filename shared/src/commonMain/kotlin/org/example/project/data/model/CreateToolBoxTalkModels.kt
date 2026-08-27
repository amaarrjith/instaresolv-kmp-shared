package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateToolBoxTalkRequest(
    val date: String,
    val startTime: String,
    val endTime: String,
    val topic: String,
    val discussionPoints: List<DiscussionPointRequest>,
    val attendees: List<ToolBoxAttendeeRequest>,
    val createdAt: String,
    val facilitiesId: String? = null,
    val images: List<ToolBoxTalkImageRequest>? = null,
    val reportedBy: String
)

@Serializable
data class DiscussionPointRequest(
    val id: Int,
    val point: String
)

@Serializable
data class AttendeeRequest(
    val id: String? = null,
    val employeeCode: String? = null,
    val employeeName: String? = null,
    val companyName: String? = null,
    val profession: String? = null
)

@Serializable
data class ToolBoxAttendeeRequest(
    val id: Int? = null,
    val employeeCode: String? = null,
    val employeeName: String? = null,
    val companyName: String? = null,
    val profession: String? = null
)
@Serializable
data class ToolBoxTalkImageRequest(
    val image: String,
    val description: String
)

@Serializable
data class CreateToolBoxTalkResponseData(
    val toolBoxTalkId: Int,
    val statusMessage: String? = null
)

@Serializable
data class CreateToolBoxTalkDraftRequest(
    val id: Long = 0,
    val facilitiesId: Int?,
    val projectJson: String?,
    val dateMillis: Long?,
    val startTime: String?,
    val endTime: String?,
    val topic: String?,
    val discussionPointsJson: String?,
    val attendeesJson: String?,
    val imagesJson: String?,
    val reportedBy: String?,
    val createdAt: String?,
    val userId: Int
)
