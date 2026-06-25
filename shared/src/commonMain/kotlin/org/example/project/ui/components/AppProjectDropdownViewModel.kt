package org.example.project.ui.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.data.model.Project
import org.example.project.domain.repository.ProjectRepository
import org.example.project.network.NetworkResult

data class AppProjectDropdownUiState(
    val isLoading: Boolean = false,
    val projects: List<Project> = emptyList(),
    val error: String? = null
)

class AppProjectDropdownViewModel(
    private val projectRepository: ProjectRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AppProjectDropdownUiState())
    val uiState: StateFlow<AppProjectDropdownUiState> = _uiState.asStateFlow()

    init {
        fetchProjects()
    }

    private fun fetchProjects() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = projectRepository.getProjects(
                searchKey = "",
                isProjectList = false,
                isInvite = false
            )
            when (result) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            projects = result.data.groups
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }
}
