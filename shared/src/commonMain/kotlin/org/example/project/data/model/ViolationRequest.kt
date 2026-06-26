package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ViolationListRequest(
    val searchKey: String? = null,
    val pageNumber: Int? = null,
    val limit: Int? = null,
    val sortType: Int? = null,
    val projectIds: List<Int>? = null,
    val openDate: String? = null,
    val endDate: String? = null,
    val reportedByPersons: List<Int>? = null
)

@Serializable
data class ViolationDetailRequest(
    val id: Int
)
