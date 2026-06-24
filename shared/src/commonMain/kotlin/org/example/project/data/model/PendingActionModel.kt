package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PendingActionRequest(
    val dummy: String? = null // Dummy parameter for empty body POST
)

@Serializable
data class PendingActionListResponse(
    val hasError: Boolean,
    val errorCode: Int,
    val message: String,
    val response: PendingActionData? = null
)

@Serializable
data class PendingActionData(
    val pendingActions: List<PendingActionItem>,
    val notificationUnReadCount: Int? = null,
    val pendingActionsCount: Int? = null
)

@Serializable
data class PendingActionItem(
    val id: Int,
    val type: Int,
    val contentId: Int,
    val date: String? = null,
    val description: String? = null,
    val groupCode: String? = null,
    val userId: Int? = null,
    val justification: String? = null
)

object PendingActionStatusType {
    const val OPEN_OBSERVATION = 1
    const val REQUEST_TO_JOIN_GROUP = 2
    const val OBSERVATION_RESPONSIBILITY_CHANGE = 3
    const val REQUEST_TO_DELETE_OBSERVATION = 4
    const val REVIEW_OBSERVATION_CLOSEOUT = 5
}
