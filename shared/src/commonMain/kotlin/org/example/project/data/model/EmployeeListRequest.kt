package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class EmployeeListRequest(
    val pageNumber: Int,
    val searchKey: String,
    val groupId: String,
    val sortType: Int
)
