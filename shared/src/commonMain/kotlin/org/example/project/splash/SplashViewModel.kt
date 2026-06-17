package org.example.project.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.data.settings.AuthPreferences
import org.example.project.domain.repository.AuthRepository
import org.example.project.network.NetworkResult
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class SplashViewModel(
    private val authPreferences: AuthPreferences,
    private val repository: AuthRepository
): ViewModel() {

    init {
        userCheckout()
    }
    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState = _uiState.asStateFlow()
    fun isLoggedIn(): Boolean {
        return authPreferences.isLoggedIn()
    }

    fun isWelcomePageShown(): Boolean {
        return authPreferences.getWelcomePageShownStatus()
    }

    @OptIn(ExperimentalUuidApi::class)
     fun userCheckout() {
        val uuid = Uuid.random().toString().replace("-", "")
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    isLoading = true
                )
            }
            val result = repository.userCheckOut(uuid)
            when(result){
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            loadingCompleted = true
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }
}