package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class NotificationListResponse(
    val notifications:ArrayList<NotificationListModel>,
    val notificationUnReadCount:Int?,
    val pendingActionsCount:Int?
) {
}

@Serializable
data class NotificationListModel(
    val id: Int,
    val type:Int,
    val contentId: Int,
    val title: String?,
    val time:String?,
    val date: String?,
    val description:String?,
    val groupCode: String?,
    var isRead: Boolean
)