package org.example.project.project
import org.example.project.data.model.Project
import org.example.project.data.model.ProjectDetail

sealed class ProjectDetailUiState {
    data object Loading : ProjectDetailUiState()
    data class Error(val errorMessage: String) : ProjectDetailUiState()
    data class Success(val project: ProjectDetail) : ProjectDetailUiState()
}