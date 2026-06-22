package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ViewProjectRequest(
    val groupId: String,
    val groupShortCode: String,
    val notificationId: Int? = null
)

@Serializable
data class ViewProjectResponse(
    val groupVerified: Boolean,
    val statusMessage: String,
    val group: Project? = null,
    val notificationUnReadCount: Int,
    val pendingActionsCount: Int
)