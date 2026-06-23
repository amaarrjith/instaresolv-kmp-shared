package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ExitProjectRequest(
    val groupCode: String
)