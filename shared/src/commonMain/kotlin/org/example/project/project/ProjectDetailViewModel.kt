package org.example.project.project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.data.settings.AuthPreferences
import org.example.project.domain.repository.ProjectRepository
import org.example.project.network.NetworkResult

class ProjectDetailViewModel(
    private val projectRepository: ProjectRepository,
    private val preferences: AuthPreferences
): ViewModel() {
    private val _uiState = MutableStateFlow<ProjectDetailUiState>(ProjectDetailUiState.Loading)
    val uiState = _uiState.asStateFlow()

    val loggedInUser = preferences.getLoggedInUser()
    fun getProjectDetails(
        groupId: Int,
        groupCode: String
    ) {
        viewModelScope.launch {
            _uiState.value = ProjectDetailUiState.Loading
            val response = projectRepository.getProjectDetails(
                groupId = groupId,
                groupCode = groupCode
            )
            when(response) {
                is NetworkResult.Success -> {
                    _uiState.value = ProjectDetailUiState.Success(response.data)
                }
                is NetworkResult.Error -> {
                    _uiState.value = ProjectDetailUiState.Error(response.message)
                }
            }
        }
    }

    private val _inviteStatus = MutableStateFlow<InviteStatus>(InviteStatus.Idle)
    val inviteStatus = _inviteStatus.asStateFlow()

    fun inviteMembers(emails: List<String>) {
        val currentState = _uiState.value
        if (currentState !is ProjectDetailUiState.Success) return

        val groupId = currentState.project.groupId
        val groupCode = currentState.project.groupCode

        viewModelScope.launch {
            _inviteStatus.value = InviteStatus.Loading
            val response = projectRepository.inviteMembers(
                groupId = groupId,
                groupCode = groupCode,
                usersEmails = emails
            )
            when(response) {
                is NetworkResult.Success -> {
                    _inviteStatus.value = InviteStatus.Success
                    getProjectDetails(groupId, groupCode)
                }
                is NetworkResult.Error -> {
                    _inviteStatus.value = InviteStatus.Error(response.message)
                }
            }
        }
    }

    fun clearInviteStatus() {
        _inviteStatus.value = InviteStatus.Idle
    }

    fun deleteProject(password: String, onSuccess:(String) -> Unit, onError: (String) -> Unit) {
        val currentState = _uiState.value
        if (currentState !is ProjectDetailUiState.Success) return

        val groupId = currentState.project.groupId
        val groupCode = currentState.project.groupCode
        viewModelScope.launch {
            val response = projectRepository.deleteProject(
                groupId = groupId,
                groupCode = groupCode,
                password = password
            )
            when(response) {
                is NetworkResult.Success -> {
                    if (response.data.isSuccess == true) {
                        onSuccess(response.data.statusMessage ?: "")
                    } else {
                        onError(response.data.statusMessage ?: "")
                    }
                }
                is NetworkResult.Error -> {
                    onError(response.message)
                }
            }
        }
    }

    fun exitProject(onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        val currentState = _uiState.value
        if (currentState !is ProjectDetailUiState.Success) return
        val groupCode = currentState.project.groupCode
        viewModelScope.launch {
            val response = projectRepository.exitProject(
                groupCode = groupCode
            )
            when(response) {
                is NetworkResult.Success -> {
                    if (response.data.isSuccess == true) {
                        onSuccess(response.data.statusMessage ?: "")
                    } else {
                        onError(response.data.statusMessage ?: "")
                    }
                }
                is NetworkResult.Error -> {
                    onError(response.message)
                }
            }
        }
    }
    fun changeMemberRole(userId: Int, newRole: Int, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        val currentState = _uiState.value
        if (currentState !is ProjectDetailUiState.Success) return
        val groupId = currentState.project.groupId
        val groupCode = currentState.project.groupCode
        viewModelScope.launch {
            val response = projectRepository.changeMemberRole(
                userId = userId,
                groupId = groupId,
                groupCode = groupCode,
                newRole = newRole
            )
            when(response) {
                is NetworkResult.Success -> {
                    if (response.data.isSuccess == true) {
                        onSuccess(response.data.statusMessage?: "")
                        getProjectDetails(groupId, groupCode)
                    } else {
                        onError(response.data.statusMessage?: "")
                    }
                }
                is NetworkResult.Error -> {
                    onError(response.message)
                }
            }
        }
    }

    fun removeMember(userId: Int, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        val currentState = _uiState.value
        if (currentState !is ProjectDetailUiState.Success) return
        val groupId = currentState.project.groupId
        val groupCode = currentState.project.groupCode
        viewModelScope.launch {
            val response = projectRepository.removeMember(
                groupId = groupId,
                groupCode = groupCode,
                userId = userId
            )
            when(response) {
                is NetworkResult.Success -> {
                    if (response.data.isSuccess == true) {
                        onSuccess(response.data.statusMessage?: "")
                        getProjectDetails(groupId, groupCode)
                    } else {
                        onError(response.data.statusMessage?: "")
                    }
                }
                is NetworkResult.Error -> {
                    onError(response.message)
                }
            }
        }
    }

    fun transferAdmin(password: String, handOverTo: Int, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        val currentState = _uiState.value
        if (currentState !is ProjectDetailUiState.Success) return
        val groupId = currentState.project.groupId
        val groupCode = currentState.project.groupCode
        viewModelScope.launch {
            val response = projectRepository.handoverSuperAdmin(
                password = password,
                groupId = groupId,
                groupCode = groupCode,
                handOverTo = handOverTo
            )
            when(response) {
                is NetworkResult.Success -> {
                    if (response.data.isSuccess == true) {
                        onSuccess(response.data.statusMessage?: "")
                        getProjectDetails(groupId, groupCode)
                    } else {
                        onError(response.data.statusMessage?: "")
                    }
                }
                is NetworkResult.Error -> {
                    onError(response.message)
                }
            }
        }
    }
}

sealed class InviteStatus {
    object Idle : InviteStatus()
    object Loading : InviteStatus()
    object Success : InviteStatus()
    data class Error(val message: String) : InviteStatus()
}
