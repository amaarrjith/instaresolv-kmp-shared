package org.example.project.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.data.model.ContactMessageRequest
import org.example.project.domain.repository.AuthRepository
import org.example.project.network.NetworkResult

data class ContactUsUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val email: String? = null,
    val address: String? = null,
    val phone: String? = null
)

class ContactUsViewModel(
    private val repository: AuthRepository
) : ViewModel() {


    private val _uiState = MutableStateFlow(ContactUsUiState())
    val uiState: StateFlow<ContactUsUiState> = _uiState.asStateFlow()

    init {
        getContactDetails()
    }

    fun sendMessage(name: String, email: String, message: String) {
        if (name.isBlank() || email.isBlank() || message.isBlank()) {
            _uiState.update { it.copy(error = "Please fill all fields") }
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, isSuccess = false) }
            val request = ContactMessageRequest(email = email, message = message, name = name)
            
            when (val result = repository.sendContactMessage(request)) {
                is NetworkResult.Success -> {
                    if (result.data.isSuccess == true) {
                        _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                    } else {
                        _uiState.update { it.copy(isLoading = false, error = result.data.statusMessage ?: "Failed to send message") }
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message ?: "Network error") }
                }
            }
        }
    }

    fun getContactDetails() {
        viewModelScope.launch {
//            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = repository.getContactInfo()
            when(result) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, email = result.data.email, address = result.data.address, phone = result.data.phone) }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message ?: "Network error") }
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
    fun resetSuccess() {
        _uiState.update { it.copy(isSuccess = false) }
    }
}
