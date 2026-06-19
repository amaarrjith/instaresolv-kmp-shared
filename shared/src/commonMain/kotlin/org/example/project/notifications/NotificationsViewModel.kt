package org.example.project.notifications

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.domain.repository.AuthRepository
import org.example.project.network.NetworkResult

class NotificationsViewModel(
    private val authRepository: AuthRepository
): ViewModel() {
    private val _uiState = MutableStateFlow<NotificationUiState>(NotificationUiState.Loading)
    val uiState = _uiState.asStateFlow()
    init {
        getNotifications()
    }
    fun getNotifications() {
        viewModelScope.launch {
            _uiState.value = NotificationUiState.Loading
            val response = authRepository.getNotificationList()
            when(response) {
                is NetworkResult.Success -> {
                    _uiState.value = NotificationUiState.Success(response.data)
                }
                is NetworkResult.Error -> {
                    _uiState.value = NotificationUiState.Error(response.message)
                }
            }
        }
    }
}