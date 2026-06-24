package org.example.project.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.data.model.DeleteAccountRequest
import org.example.project.data.settings.AuthPreferences
import org.example.project.domain.repository.AuthRepository
import org.example.project.network.NetworkResult

enum class DeleteAccountStep {
    FETCHING_TERMS,
    TERMS_LOADED,
    OTP_SENT
}

data class DeleteAccountUiState(
    val isLoading: Boolean = false,
    val terms: String? = null,
    val step: DeleteAccountStep = DeleteAccountStep.FETCHING_TERMS,
    val error: String? = null,
    val isSuccess: Boolean = false
)

class DeleteAccountViewModel(
    private val repository: AuthRepository,
    private val authPreferences: AuthPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeleteAccountUiState())
    val uiState: StateFlow<DeleteAccountUiState> = _uiState

    init {
        fetchTerms()
    }

    private fun fetchTerms() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, step = DeleteAccountStep.FETCHING_TERMS) }
            val userId = authPreferences.getLoggedInUser()?.userId ?: -1
            val isGuestUser = authPreferences.getLoggedInUser()?.userType == 0 // Assuming guest if userType is 0, or just false for now
            // Or maybe just false, the app logic implies we might not have guest users in settings.
            val request = org.example.project.data.model.DeleteAccountTermsRequest(
                isGuestUser = false, // as requested: {"isGuestUser":false,"userId":9}
                userId = userId
            )
            
            when (val result = repository.getDeleteAccountTerms(request)) {
                is NetworkResult.Success -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            terms = result.data.content, 
                            step = DeleteAccountStep.TERMS_LOADED 
                        ) 
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update { 
                        it.copy(isLoading = false, error = result.message) 
                    }
                }
            }
        }
    }

    fun requestDelete(password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val refresh = authPreferences.getRefreshToken() ?: ""
            val request = DeleteAccountRequest(
                password = password,
                refresh = refresh
            )
            when (val result = repository.requestDeleteAccount(request)) {
                is NetworkResult.Success -> {
                    _uiState.update { 
                        it.copy(isLoading = false, step = DeleteAccountStep.OTP_SENT) 
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update { 
                        it.copy(isLoading = false, error = result.message) 
                    }
                }
            }
        }
    }

    fun verifyDelete(password: String, otp: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val refresh = authPreferences.getRefreshToken() ?: ""
            val request = DeleteAccountRequest(
                password = password,
                otp = otp,
                refresh = refresh
            )
            when (val result = repository.verifyDeleteAccount(request)) {
                is NetworkResult.Success -> {
                    authPreferences.logout()
                    _uiState.update { 
                        it.copy(isLoading = false, isSuccess = true) 
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update { 
                        it.copy(isLoading = false, error = result.message) 
                    }
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
