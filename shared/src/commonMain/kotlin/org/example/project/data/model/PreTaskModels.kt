package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PreTaskListRequest(
    val searchKey: String = "",
    val pageNumber: Int = 1,
    val limit: Int = 20,
    val sortType: Int = 1,
    val projectIds: List<Int> = emptyList(),
    val openDate: String = "",
    val endDate: String = "",
    val reportedByPersons: List<Int> = emptyList()
)

@Serializable
data class PreTaskData(
    val id: Int,
    val date: String? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val msraReference: String? = null,
    val permitReference: String? = null,
    val taskTitle: String? = null,
    val createdAt: String? = null,
    val facilities: PreTaskFacility? = null,
    val images: List<PreTaskImage>? = null,
    val reportedBy: String? = null,
    val notes: String? = null
)

@Serializable
data class PreTaskFacility(
    val groupId: String? = null,
    val groupName: String? = null,
    val groupCode: String? = null,
    val groupImage: String? = null
)

@Serializable
data class PreTaskImage(
    val image: String? = null,
    val description: String? = null
)

@Serializable
data class PreTaskContentRequest(
    val contentUpdatedTime: String = "",
    val questionUpdatedTime: String = ""
)

@Serializable
data class PreTaskContentResponseData(
    val contents: List<PreTaskContentData>? = null,
    val questions: List<PreTaskQuestionData>? = null,
    val deletedContentsId: List<Int>? = null,
    val deletedQuestionsId: List<Int>? = null,
    val isContentEmpty: Boolean = false,
    val isQuestionEmpty: Boolean = false
)

@Serializable
data class PreTaskContentData(
    val id: Int,
    val title: String? = null,
    val updatedTime: String? = null,
    val order: Int? = null
)

@Serializable
data class PreTaskQuestionData(
    val id: Int,
    val contentId: Int? = null,
    val title: String? = null,
    val imageURL: String? = null,
    val updatedTime: String? = null
)

@Serializable
data class CreatePreTaskRequest(
    val date: String,
    val startTime: String,
    val endTime: String,
    val msraReference: String, 
    val permitReference: String,
    val taskTitle: String,
    val contents: List<CreatePreTaskContent>,
    val questions: List<CreatePreTaskQuestion>,
    val otherTopic: List<CreatePreTaskOtherTopic>,
    val attendees: List<CreatePreTaskAttendee>,
    val createdAt: String,
    val facilitiesId: String,
    val images: List<CreatePreTaskImage>,
    val reportedBy: String,
    val notes: String,
    val sendNotificationTo: List<Int>
)

@Serializable
data class CreatePreTaskContent(
    val id: Int,
    val title: String
)

@Serializable
data class CreatePreTaskQuestion(
    val id: Int,
    val contentId: Int,
    val title: String,
    val imageUrl: String,
    val selectedAnswer: Int
)

@Serializable
data class CreatePreTaskOtherTopic(
    val id: Int,
    val title: String,
    val selectedAnswer: Int
)

@Serializable
data class CreatePreTaskAttendee(
    val id: Int,
    val employeeCode: String,
    val employeeName: String,
    val companyName: String,
    val profession: String
)

@Serializable
data class CreatePreTaskImage(
    val image: String,
    val description: String,
    val isAiGeneratedDescription: Boolean
)

@Serializable
data class CreatePreTaskResponseData(
    val preTaskId: Int? = null,
    val statusMessage: String? = null
)

@kotlinx.serialization.Serializable
data class PreTaskDetailRequest(
    val id: Int
)

@kotlinx.serialization.Serializable
data class PreTaskDetailResponseData(
    val id: Int,
    val date: String? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val msraReference: String? = null,
    val permitReference: String? = null,
    val taskTitle: String? = null,
    val contents: List<PreTaskContentData>? = null,
    val questions: List<PreTaskQuestionDetail>? = null,
    val otherTopic: List<PreTaskOtherTopicDetail>? = null,
    val attendees: List<PreTaskAttendeeDetail>? = null,
    val createdAt: String? = null,
    val facilities: PreTaskFacilityData? = null,
    val images: List<PreTaskImageDetail>? = null,
    val reportedBy: String? = null,
    val notes: String? = null,
    val translatedNotes: String? = null
)

@kotlinx.serialization.Serializable
data class PreTaskQuestionDetail(
    val id: Int,
    val contentId: Int? = null,
    val title: String? = null,
    val imageUrl: String? = null,
    val selectedAnswer: Int? = null
)

@kotlinx.serialization.Serializable
data class PreTaskOtherTopicDetail(
    val id: Int,
    val title: String? = null,
    val selectedAnswer: Int? = null
)

@kotlinx.serialization.Serializable
data class PreTaskAttendeeDetail(
    val id: String? = null,
    val employeeCode: String? = null,
    val employeeName: String? = null,
    val companyName: String? = null,
    val profession: String? = null
)

@kotlinx.serialization.Serializable
data class PreTaskImageDetail(
    val image: String? = null,
    val description: String? = null
)

@kotlinx.serialization.Serializable
data class PreTaskFacilityData(
    val groupId: String? = null,
    val groupName: String? = null,
    val groupCode: String? = null,
    val groupImage: String? = null
)
