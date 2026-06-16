package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class OTPResponse(
    val statusMessage: String?,
    val otpVerified: Boolean,
    val uuid: String,
    val user: User? = null,
    val settings: Settings? = null,
    val auth: Authentication? = null
)
@Serializable
data class User(
    val userId: Int,
    val name: String,
    val email: String,
    val profileImage: String,
    val designation: String,
    val company: String,
    val companylogo: String,
    val userRole: Int
)
@Serializable
data class Settings(
    val emailNotification: Boolean,
    val appNotification: Boolean,
)
@Serializable
data class Authentication(
    val access: String,
    val refresh: String,
    val tokenExpiry: Long,
    val time: String
)
