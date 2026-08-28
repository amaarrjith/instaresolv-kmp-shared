package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AppUpdateResponse(
    val android: AppUpdateData,
    val iOS: AppUpdateData
)

@Serializable
data class AppUpdateData(
    val isForceUpdate: Boolean,
    val latestVersion: String
)