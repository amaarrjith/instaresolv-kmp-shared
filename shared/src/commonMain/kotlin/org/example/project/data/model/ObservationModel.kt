package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ObservationRequest(
    val observers: List<Int> = emptyList(),
    val responsiblePersons: List<Int> = emptyList(),
    val groupIds: List<Int> = emptyList(),
    val status: Int = -1,
    val closeDate: String = "",
    val openDate: String = "",
    val searchKey: String = "",
    val groupSpecified: Int = -1,
    val pageNumber: Int = 1,
    val sortType: Int = 1,
    val notificationId: Int = -1
)

@Serializable
data class ObservationListResponse(
    val hasError: Boolean,
    val errorCode: Int,
    val message: String,
    val response: ObservationData? = null
)

@Serializable
data class ObservationData(
    val observations: List<ObservationItem> = emptyList(),
    val notificationUnReadCount: Int? = null,
    val pendingActionsCount: Int? = null
)

@Serializable
data class ObservationItem(
    val observationId: Int,
    val observationTitle: String? = null,
    val description: String? = null,
    val date: String? = null,
    val time: String? = null,
    val status: Int? = null,
    val responsiblePerson: Int? = null,
    val groupSpecified: Boolean? = null,
    val group: ObservationGroup? = null,
    val images: List<String> = emptyList(),
    val totalImages: Int = 0
)

@Serializable
data class ObservationGroup(
    val groupId: String? = null,
    val groupCode: String? = null,
    val groupImage: String? = null,
    val groupName: String? = null
)
