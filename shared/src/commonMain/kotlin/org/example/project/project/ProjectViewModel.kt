package org.example.project.project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.data.model.Project
import org.example.project.domain.repository.ProjectRepository
import org.example.project.network.NetworkResult

class ProjectViewModel(
    private val projectRepository: ProjectRepository
): ViewModel() {
    private val _uiState = MutableStateFlow<ProjectListUiState>(ProjectListUiState.Success(emptyList()))
    val uiState = _uiState.asStateFlow()

    init {
        getProjects()
    }
    fun getProjects(
        searchKey: String = "",
        isProjectList: Boolean = true,
        isInvite: Boolean = false
    ) {
        viewModelScope.launch {
            _uiState.value = ProjectListUiState.Loading
            val response = projectRepository.getProjects(
                searchKey = searchKey,
                isProjectList = isProjectList,
                isInvite = isInvite
            )
            when(response) {
                is NetworkResult.Success -> {
                    _uiState.value = ProjectListUiState.Success(
                        projectList = response.data.groups
                    )
                }
                is NetworkResult.Error -> {
                    _uiState.value = ProjectListUiState.Error(
                        errorMessage = response.message
                    )
                }
            }
        }
    }
}