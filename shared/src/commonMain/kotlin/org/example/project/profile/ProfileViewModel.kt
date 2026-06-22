package org.example.project.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.data.settings.AuthPreferences
import org.example.project.domain.repository.AuthRepository
import org.example.project.network.NetworkResult

class ProfileViewModel(
    private val preferences: AuthPreferences,
    private val repository: AuthRepository
): ViewModel() {
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Ready)
    val uiState = _uiState.asStateFlow()
    var user = preferences.getLoggedInUser()
        private set

    fun logout() {
        preferences.logout()
    }

    fun setEditMode(isEditing: Boolean) {
        _uiState.value = if (isEditing) ProfileUiState.isEditing else ProfileUiState.Ready
    }

    fun saveProfile(name: String, profileImage: String, email: String, designation: String, company: String) {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            val response = repository.editProfile(name, profileImage, company, designation)
            when(response) {
                is NetworkResult.Success -> {
                    _uiState.value = ProfileUiState.Success(
                        successMessage = "Profile Updated Successfully"
                    )
                    val updatedUser = user?.copy(
                        name = response.data.name,
                        email = email,
                        profileImage = response.data.profileImage,
                        designation = response.data.designation,
                        company = response.data.company
                    )
                    preferences.saveLoggedInUser(updatedUser)
                    user = updatedUser
                }
                is NetworkResult.Error -> {
                    _uiState.value = ProfileUiState.Error(
                        message = response.message
                    )
                }
            }
        }
    }

    fun updateUi() {
        _uiState.value = ProfileUiState.Ready
    }
}