package org.example.project.project

import org.example.project.data.model.Project

sealed class ProjectListUiState {
    object Loading : ProjectListUiState()
    data class Error(val errorMessage: String): ProjectListUiState()
    data class Success(val projectList: List<Project>): ProjectListUiState()
    data class ViewProject(val project: Project): ProjectListUiState()
}