package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class GenerateInspectionExcelRequest(
    val searchKey: String = "",
    val sortBy: Int = 1,
    val projectIds: List<Int> = emptyList(),
    val openDate: String = "",
    val endDate: String = "",
    val reportedByPersons: List<Int> = emptyList()
)

@Serializable
data class GenerateIncidentExcelRequest(
    val searchKey: String = "",
    val sortBy: Int = 1,
    val projectIds: List<Int> = emptyList(),
    val openDate: String = "",
    val endDate: String = "",
    val reportedByPersons: List<Int> = emptyList()
)

@Serializable
data class GenerateObservationExcelRequest(
    val searchKey: String = "",
    val sortBy: Int = 1,
    val projectIds: List<Int> = emptyList(),
    val openDate: String = "",
    val endDate: String = "",
    val reportedByPersons: List<Int> = emptyList()
)

@Serializable
data class GenerateViolationExcelRequest(
    val searchKey: String = "",
    val sortBy: Int = 1,
    val projectIds: List<Int> = emptyList(),
    val openDate: String = "",
    val endDate: String = "",
    val reportedByPersons: List<Int> = emptyList()
)
