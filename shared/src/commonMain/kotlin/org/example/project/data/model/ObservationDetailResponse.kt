package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ObservationDetailResponse(
    val observationTitle: String? = null,
    val status: Int? = null,
    val description: String? = null,
    val date: String? = null,
    val time: String? = null,
    val reportedBy: String? = null,
    val location: String? = null,
    val creatorId: Int? = null,
    val audioUrl: String? = null,
    val responsiblePersonName: String? = null,
    val groupSpecified: Boolean? = null,
    val isCancelable: Boolean? = null,
    val group: ObservationGroupDetail? = null,
    val notificationTo: List<ObservationNotificationUser>? = null,
    val saveAsDraft: Boolean? = null,
    val imageDescription: List<ObservationImageDescription>? = null,
    val responsiblePerson: ObservationResponsiblePerson? = null,
    val closeDetails: ObservationCloseDetails? = null,
    val notificationUnReadCount: Int? = null,
    val pendingActionsCount: Int? = null
)

@Serializable
data class ObservationGroupDetail(
    val groupId: String? = null,
    val groupCode: String? = null,
    val groupImage: String? = null,
    val groupName: String? = null,
    val userRole: Int? = null
)

@Serializable
data class ObservationNotificationUser(
    val userId: Int? = null,
    val name: String? = null,
    val email: String? = null,
    val image: String? = null,
    val role: Int? = null
)

@Serializable
data class ObservationImageDescription(
    val image: String? = null,
    val description: String? = null,
    val isAiGeneratedDescription: Boolean? = null
)

@Serializable
data class ObservationResponsiblePerson(
    val userId: Int? = null,
    val image: String? = null,
    val name: String? = null,
    val email: String? = null
)

@Serializable
data class ObservationCloseDetails(
    val date: String? = null,
    val time: String? = null,
    val closeDescription: String? = null,
    val closedBy: ObservationResponsiblePerson? = null,
    val imageDescription: List<ObservationImageDescription>? = null
)
