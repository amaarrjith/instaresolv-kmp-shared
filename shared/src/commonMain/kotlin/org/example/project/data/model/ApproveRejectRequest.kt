package org.example.project.data.model

import kotlinx.serialization.Serializable
import org.example.project.ui.screens.ObservationDetailUiState

@Serializable
data class ApproveRejectRequest (
    val id: Int,
    val action: Int
)

@Serializable
data class ApproveRejectResponse (
    val id: Int,
    val isSuccess: Boolean,
    val statusMessage: String
)