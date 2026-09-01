package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ProjectUserViewRequest(
    val userId: Int
)
