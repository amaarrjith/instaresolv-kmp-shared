package org.example.project.data.model

import kotlinx.serialization.Serializable


@Serializable
data class EmployeeData(
    val id: Int,
    val employeeCode: String? = null,
    val employeeName: String? = null,
    val companyName: String? = null,
    val profession: String? = null
)
