package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class IncidentRequest(
    val searchKey: String? = null,
    val pageNumber: Int,
    val limit: Int,
    val sortType: Int,
    val projectIds: List<Int>? = null,
    val openDate: String? = null,
    val endDate: String? = null,
    val reportedByPersons: List<Int>? = null,
    val incidentTypes: List<Int>? = null
)
