package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DeleteProjectRequest(
    val password: String,
    val groupId: Int,
    val groupCode: String
)