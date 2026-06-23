package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class InviteUsersRequest(
    val groupId: Int,
    val groupCode: String,
    val usersEmails: List<String>
)
