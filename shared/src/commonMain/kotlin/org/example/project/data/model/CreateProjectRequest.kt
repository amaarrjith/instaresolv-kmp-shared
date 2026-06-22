package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateProjectRequest(
    val groupCode: String,
    val groupName: String,
    val description: String,
    val groupImage: String
) {
}

@Serializable
data class CreateProjectResponse(
    val groupId: Int,
    val groupCode: String,
    val groupName: String,
    val description: String,
    val groupImage: String
)