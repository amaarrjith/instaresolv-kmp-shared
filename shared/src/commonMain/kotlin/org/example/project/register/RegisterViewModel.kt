package org.example.project.register

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.example.project.domain.validation.RegisterValidator
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.example.project.domain.repository.AuthRepository
import org.example.project.network.NetworkResult

class RegisterViewModel(
    private val validator: RegisterValidator,
    private val repository: AuthRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()

    fun register(
        profileImageUrl: String,
        fullName: String,
        emailId: String,
        password: String,
        confirmPassword: String,
        designation: String,
        company: String,
        isTermsAccepted: Boolean) {
        val trimmedFullName = fullName.trim()
        val trimmedEmail = emailId.trim()
        val trimmedPassword = password.trim()
        val trimmedConfirmPassword = confirmPassword.trim()
        val trimmedDesignation = designation.trim()
        val trimmedCompany = company.trim()

        viewModelScope.launch {
           _uiState.update { state ->
               state.copy(
                   isLoading = true
               )
           }
            validator.validateFullName(trimmedFullName)?.let { error ->
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        errorMessage = error
                    )
                }
                return@launch
            }
           validator.validateEmail(trimmedEmail)?.let { error ->
               _uiState.update { state ->
                   state.copy(
                       isLoading = false,
                       errorMessage = error
                   )
               }
               return@launch
           }
           validator.validatePassword(trimmedPassword)?.let { error ->
               _uiState.update { state ->
                   state.copy(
                       isLoading = false,
                       errorMessage = error
                   )
               }
               return@launch
           }
           validator.validateConfirmPassword(trimmedPassword, trimmedConfirmPassword)?.let { error ->
               _uiState.update { state ->
                   state.copy(
                       isLoading = false,
                       errorMessage = error
                   )
               }
               return@launch
           }
           validator.validateTermsAndConditions(isTermsAccepted)?.let {
               error ->
               _uiState.update { state ->
                   state.copy(
                       isLoading = false,
                       errorMessage = error
                   )
               }
               return@launch
           }
           val result = repository.register(
               profileImage = profileImageUrl,
               fullName = trimmedFullName,
               email = trimmedEmail,
               password = trimmedPassword,
               confirmPassword = trimmedConfirmPassword,
               designation = trimmedDesignation,
               company = trimmedCompany,
               isTermsAccepted = isTermsAccepted
           )
           when(result) {
               is NetworkResult.Success -> {
                   _uiState.update {
                       it.copy(
                           isLoading = false,
                           isRegisterSuccess = true,
                           tempUserId = result.data.tempUserId ?: 0,
                           email = result.data.email ?: ""
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

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }

    }
}