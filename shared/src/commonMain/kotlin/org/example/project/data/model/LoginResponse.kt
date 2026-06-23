package org.example.project.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(

    @SerialName("auth")
    val auth: AuthResponse? = null,

    @SerialName("settings")
    val settings: NotificationSettingsModel? = null,

    @SerialName("user")
    val user: UserResponse? = null,

    @SerialName("userDetails")
    val userDetails: UserDetailInfoModel? = null,

    @SerialName("message")
    val message: String? = null
)

@Serializable
data class AuthResponse(

    @SerialName("access")
    val access: String? = null,

    @SerialName("refresh")
    val refresh: String? = null,

    @SerialName("tokenExpiry")
    val tokenExpiry: Long? = null,

    @SerialName("time")
    val time: String? = null
)

@Serializable
data class UserResponse(

    @SerialName("uuid")
    val uuid: String? = null,

    @SerialName("name")
    val name: String? = null,

    @SerialName("email")
    val email: String? = null,

    @SerialName("userType")
    val userType: Int? = null,

    @SerialName("profileImage")
    val profileImage: String? = null,

    @SerialName("userId")
    val userId: Int? = null,

    @SerialName("designation")
    val designation: String? = null,

    @SerialName("company")
    val company: String? = null,

    @SerialName("userRole")
    val userRole: Int = 0,

    @SerialName("projectDesignation")
    val projectDesignation: List<Int> = emptyList()
)

@Serializable
data class UserDetailInfoModel(

    @SerialName("companyName")
    val companyName: String? = null,

    @SerialName("companyLogo")
    val companyLogo: String? = null,

    @SerialName("notificationUnReadCount")
    val notificationUnReadCount: Int? = null,

    @SerialName("pendingActionsCount")
    val pendingActionsCount: Int? = null
)

@Serializable
data class NotificationSettingsModel(

    @SerialName("emailNotification")
    val emailNotification: Boolean? = null,

    @SerialName("appNotification")
    val appNotification: Boolean? = null
)


enum class UserType(val value: Int) {
    NORMAL_USER(1),
    APP_ADMIN(2),
    ADMIN(3);

    companion object {
        fun fromInt(value: Int): UserType {
            return entries.firstOrNull { it.value == value }
                ?: NORMAL_USER
        }
    }
}
