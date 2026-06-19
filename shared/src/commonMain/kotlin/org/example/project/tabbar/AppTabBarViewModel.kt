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
    
    private val _uiState = MutableStateFlow<AppTabBarUiState>(AppTabBarUiState.Success())
    val uiState = _uiState.asStateFlow()

    init {
        getHomeContents()
    }
    fun getHomeContents() {
        val  userId = authPreferences.getLoggedInUser()?.userId ?: -1
        viewModelScope.launch {
            val response = authRepository.getHomeContents(userId)
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