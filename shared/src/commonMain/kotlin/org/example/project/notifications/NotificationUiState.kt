package org.example.project.notifications

import org.example.project.data.model.NotificationListResponse

sealed class NotificationUiState {
    object Loading : NotificationUiState()
    data class Success(val response: NotificationListResponse) : NotificationUiState()
    data class Error(val errorMessage: String) : NotificationUiState()
}