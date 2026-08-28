package org.example.project.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.data.settings.AuthPreferences
import org.example.project.domain.repository.AuthRepository
import org.example.project.getPlatform
import org.example.project.network.NetworkResult
import org.example.project.utilites.getAppInfo
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class SplashViewModel(
    private val authPreferences: AuthPreferences,
    private val repository: AuthRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState = _uiState.asStateFlow()

    init {
        userCheckout()
    }

    fun isLoggedIn(): Boolean {
        return authPreferences.isLoggedIn()
    }

    fun isWelcomePageShown(): Boolean {
        return authPreferences.getWelcomePageShownStatus()
    }

    @OptIn(ExperimentalUuidApi::class)
     fun userCheckout() {
        val targetUuid = authPreferences.getLoggedInUser()?.uuid
            ?.takeIf { it.isNotBlank() }
            ?: Uuid.random().toString().replace("-", "")
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    isLoading = true
                )
            }
            val result = repository.userCheckOut(targetUuid)
            when(result){
                is NetworkResult.Success -> {
                    checkAppUpdates { isUpdateNeeded ->
                        if (isUpdateNeeded) {
                            _uiState.update {
                                it.copy(
                                    isUpdateAvailable = true
                                )
                            }
                        } else {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    loadingCompleted = true
                                )
                            }
                        }
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

    fun onRetry() {
        _uiState.update {
            it.copy(
                isLoading = false,
                errorMessage = null
            )
        }
        userCheckout()
    }

    fun checkAppUpdates(updateNeeded: (Boolean) -> Unit) {
        val platformName = getPlatform().name
        val isIos = platformName.contains("iOS", ignoreCase = true)
        val currentAppVersion = getAppInfo().appVersion
        viewModelScope.launch {
            val result = repository.checkAppUpdate()
            when(result) {
                is NetworkResult.Success -> {
                    if (isIos) {
                        val isForceUpdate = result.data.iOS.isForceUpdate
                        val isUpdateNeeded = isUpdateNeeded(currentAppVersion, result.data.iOS.latestVersion)
                        updateNeeded(isForceUpdate && isUpdateNeeded)
                    } else {
                        val isForceUpdate = result.data.android.isForceUpdate
                        val isUpdateNeeded = isUpdateNeeded(currentAppVersion, result.data.android.latestVersion)
                        updateNeeded(isForceUpdate && isUpdateNeeded)
                    }
                }
                is NetworkResult.Error -> {
                    println("Error checking app updates: ${result.message}")
                    updateNeeded(false)
                }
            }
        }
    }

    private fun isUpdateNeeded(current: String, latest: String): Boolean {
        val currentParts = current.split(".").map { part ->
            part.takeWhile { it.isDigit() }.toIntOrNull() ?: 0
        }
        val latestParts = latest.split(".").map { part ->
            part.takeWhile { it.isDigit() }.toIntOrNull() ?: 0
        }
        val maxLength = maxOf(currentParts.size, latestParts.size)
        for (i in 0 until maxLength) {
            val currentPart = currentParts.getOrElse(i) { 0 }
            val latestPart = latestParts.getOrElse(i) { 0 }
            if (latestPart > currentPart) return true
            if (currentPart > latestPart) return false
        }
        return false
    }
}