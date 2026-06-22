package org.example.project.project

import org.example.project.data.model.CreateProjectResponse

sealed class CreateProjectUiState {
    data object Ready: CreateProjectUiState()
    data object isLoading : CreateProjectUiState()
    data class Success(val project: CreateProjectResponse) : CreateProjectUiState()
    data class Error(val errorMessage: String) : CreateProjectUiState()
}