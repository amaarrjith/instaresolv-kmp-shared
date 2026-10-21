package org.example.project.project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.data.model.CreateProjectResponse
import org.example.project.domain.repository.ProjectRepository
import org.example.project.network.NetworkResult

import org.example.project.domain.repository.AuthRepository
import org.jetbrains.compose.resources.getString
import instaresolv.shared.generated.resources.*

class CreateProjectViewModel(
    private val projectRepository: ProjectRepository,
    private val authRepository: AuthRepository
): ViewModel() {
    private val _uiState = MutableStateFlow<CreateProjectUiState>(CreateProjectUiState.Ready)
    val uiState = _uiState.asStateFlow()

    private val _uploadedImageUrl = MutableStateFlow<String>("")
    val uploadedImageUrl = _uploadedImageUrl.asStateFlow()

    fun setUploadedImageUrl(url: String) {
        _uploadedImageUrl.value = url
    }

    fun createProject(
        groupCode: String,
        groupName: String,
        description: String,
        groupImage: String
    ) {
        viewModelScope.launch {
            if (groupName.trim().isEmpty()) {
                _uiState.value = CreateProjectUiState.Error(getString(Res.string.error_provide_project_name))
                return@launch
            }
            _uiState.value = CreateProjectUiState.isLoading
            val response = projectRepository.createProject(
                groupCode = groupCode,
                groupName = groupName,
                description = description,
                groupImage = groupImage
            )
            when(response) {
                is NetworkResult.Success -> {
                    _uiState.value = CreateProjectUiState.Success(response.data)
                }
                is NetworkResult.Error -> {
                    _uiState.value = CreateProjectUiState.Error(response.message)
                }
            }
        }
    }

    fun clearState() {
        _uiState.value = CreateProjectUiState.Ready
    }
}