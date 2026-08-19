package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class EmployeeAllListRequest(
    val searchKey: String,
    val pageNumber: Int
)
