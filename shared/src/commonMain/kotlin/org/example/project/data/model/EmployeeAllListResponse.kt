package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class EmployeeAllListResponse(
    val hasError: Boolean = false,
    val errorCode: Int? = null,
    val message: String? = null,
    val response: List<EmployeeData>? = null
)
