package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ProjectAccessRequest(
    val groupId: String,
    val groupShortCode: String
)

@Serializable
data class ProjectAccessResponse(
    val groupVerified: Boolean,
    val statusMessage: String
)
