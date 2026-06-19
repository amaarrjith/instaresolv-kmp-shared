package org.example.project.tabbar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.data.settings.AuthPreferences
import org.example.project.domain.repository.AuthRepository
import org.example.project.network.NetworkResult

class AppTabBarViewModel(
    private val authRepository: AuthRepository,
    private val authPreferences: AuthPreferences
): ViewModel() {
    
    private val _uiState = MutableStateFlow<AppTabBarUiState>(AppTabBarUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    init {
        getHomeContents(showRefreshSpinner = false)
    }
    fun getHomeContents(showRefreshSpinner: Boolean = true) {
        val  userId = authPreferences.getLoggedInUser()?.userId ?: -1
        viewModelScope.launch {
            if (showRefreshSpinner) {
                _isRefreshing.value = true
            } else if (_uiState.value !is AppTabBarUiState.Success) {
                _uiState.value = AppTabBarUiState.Loading
            }
            val response = authRepository.getHomeContents(userId)
            if (showRefreshSpinner) {
                _isRefreshing.value = false
            }
            when(response) {
                is NetworkResult.Success -> {
                    _uiState.value = AppTabBarUiState.Success(
                        actionsOverview = response.data.actionsOverview,
                        assignedToMe = response.data.assignedToMe
                    )
                }
                is NetworkResult.Error -> {
                    _uiState.value = AppTabBarUiState.Error(
                        response.message
                    )
                }
            }
        }
    }
}