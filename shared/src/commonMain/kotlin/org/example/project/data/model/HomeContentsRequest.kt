package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class HomeContentsRequest(
    val userId: Int
)

@Serializable
data class HomeResponse(
    val actionsOverview: ActionsOverview? = null,
    val assignedToMe: AssignedToMe? = null
)

@Serializable
data class ActionsOverview(
    val auditAndInspectionsCount: Int = 0,
    val permitToWorkCount: Int = 0,
    val observationsCount: Int = 0,
    val incidentCount: Int = 0,
    val violationCount: Int = 0,
    val preTaskCount: Int = 0,
    val toolboxCount: Int = 0,
    val lessonCount: Int = 0,
    val pendingActionsCount: Int = 0,
    val trainingsCount: Int = 0,
    val unreadNotificationsCount: Int = 0
)

@Serializable
data class AssignedToMe(
    val observation: PendingObservation? = null,
    val permit: PendingPermit? = null
)

@Serializable
data class PendingObservation(
    val contentId: Int = 0,
    val date: String = "",
    val imageUrl: String = "",
    val pendingActionType: Int = 0,
    val reportedBy: ReportedBy? = null,
    val title: String = "",
    val groupCode: String? = null,
    val groupId: Int? = null,
    val pendingActionId: Int? = null,
    val justification: String? = null
)

@Serializable
data class PendingPermit(
    val id: Int = 0,
    val createdAt: String = "",
    val groupCode: String = "",
    val permitCode: String = "",
    val permitId: Int = 0,
    val permitType: PermitType? = null,
    val status: Int = 0
)

@Serializable
data class ReportedBy(
    val imageUrl: String = "",
    val name: String = ""
)

