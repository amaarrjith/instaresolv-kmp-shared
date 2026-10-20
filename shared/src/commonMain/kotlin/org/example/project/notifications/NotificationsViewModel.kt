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

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    init {
        getNotifications()
    }

    fun getNotifications(isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (isRefresh) {
                _isRefreshing.value = true
            } else {
                _uiState.value = NotificationUiState.Loading
            }
            val response = authRepository.getNotificationList()
            if (isRefresh) {
                _isRefreshing.value = false
            }
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