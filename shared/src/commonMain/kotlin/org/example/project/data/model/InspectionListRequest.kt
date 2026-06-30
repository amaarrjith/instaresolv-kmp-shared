package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class InspectionListRequest(
    val searchKey: String = "",
    val page: Int = 1,
    val limit: Int = 20,
    val sortType: Int = 1,
    val projectIds: List<Int> = emptyList(),
    val openDate: String = "",
    val endDate: String = "",
    val reportedByPersons: List<Int> = emptyList()
)
