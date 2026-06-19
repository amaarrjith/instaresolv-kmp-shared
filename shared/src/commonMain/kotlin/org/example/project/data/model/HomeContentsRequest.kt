package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class HomeContentsRequest(
    val userId: Int
)

@Serializable
data class HomeResponse(
    val actionsOverview: ActionsOverview,
    val assignedToMe: AssignedToMe
)

@Serializable
data class ActionsOverview(
    val auditAndInspectionsCount: Int,
    val permitToWorkCount: Int,
    val observationsCount: Int,
    val incidentCount: Int,
    val violationCount: Int,
    val preTaskCount: Int,
    val toolboxCount: Int,
    val lessonCount: Int,
    val pendingActionsCount: Int,
    val trainingsCount: Int,
    val unreadNotificationsCount: Int
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

