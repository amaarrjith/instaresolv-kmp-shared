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
    val observation: PendingObservation
)

@Serializable
data class PendingObservation(
    val contentId: Int,
    val date: String,
    val imageUrl: String,
    val pendingActionType: Int,
    val reportedBy: ReportedBy,
    val title: String
)

@Serializable
data class ReportedBy(
    val imageUrl: String,
    val name: String
)

