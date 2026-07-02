package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateLessonLearnedRequest(
    val faciltiesId: String? = null,
    val title: String,
    val description: String? = null,
    val images: List<LessonLearnedImageRequest>? = null,
    val reportedBy: String
)

@Serializable
data class LessonLearnedImageRequest(
    val image: String,
    val description: String,
    val isAiGeneratedDescription: Boolean
)

@Serializable
data class CreateLessonLearnedResponseData(
    val lessonId: Int,
    val statusMessage: String? = null
)

@Serializable
data class LessonLearnedListRequest(
    val searchKey: String? = null,
    val pageNumber: Int,
    val limit: Int,
    val sortType: Int,
    val projectIds: List<Int>? = null,
    val openDate: String? = null,
    val endDate: String? = null,
    val reportedByPersons: List<Int>? = null
)

@Serializable
data class LessonLearnedData(
    val id: Int,
    val reportedBy: String? = null,
    val title: String? = null,
    val description: String? = null,
    val createdAt: String? = null,
    val facilities: LessonLearnedFacilityData? = null,
    val images: List<LessonLearnedImageData>? = null
)

@Serializable
data class LessonLearnedFacilityData(
    val groupId: String? = null,
    val groupName: String? = null,
    val groupCode: String? = null,
    val groupImage: String? = null
)

@Serializable
data class LessonLearnedImageData(
    val image: String? = null,
    val description: String? = null
)

@Serializable
data class LessonLearnedDetailRequest(
    val id: Int
)

@Serializable
data class LessonLearnedDetailResponseData(
    val id: Int,
    val reportedBy: String? = null,
    val title: String? = null,
    val description: String? = null,
    val translatedDescription: String? = null,
    val createdAt: String? = null,
    val facilities: LessonLearnedFacilityData? = null,
    val images: List<LessonLearnedImageData>? = null
)
