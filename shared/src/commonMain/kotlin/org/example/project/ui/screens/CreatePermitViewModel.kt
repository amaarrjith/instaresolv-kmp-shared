package org.example.project.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.data.model.PermitContentItem
import org.example.project.data.model.PermitContentRequest
import org.example.project.data.settings.AuthPreferences
import org.example.project.domain.repository.PermitRepository
import org.example.project.network.NetworkResult
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.project.data.model.PermitValidityCondition
import org.example.project.data.model.PermitValidityProject
import org.example.project.data.model.PermitValiditySection
import org.example.project.data.model.PermitValiditySubmitRequest
import org.example.project.data.model.toProject
import org.example.project.data.model.toGroupUser
import org.example.project.utilities.convertTo24HourFormat
import org.example.project.utilities.formatTimestamp
import kotlin.collections.emptyList

data class CreatePermitUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val certificateValidity: List<PermitContentItem> = emptyList(),
    val generalConditions: List<PermitContentItem> = emptyList(),
    val certificateValidityAnswers: Map<Int, String> = emptyMap(),
    val generalConditionAnswers: Map<Int, String> = emptyMap(),
    val generalConditionRemarks: Map<Int, String> = emptyMap(),
    val signatureUrl: String? = null,
    val signatureDateMillis: Long? = null,
    val signatureTime: String = "",
    val projects: List<org.example.project.data.model.Project> = emptyList(),
    val authorizedUsers: List<org.example.project.data.model.GroupUser> = emptyList(),
    val selectedProject: org.example.project.data.model.Project? = null,
    val selectedUser: org.example.project.data.model.GroupUser? = null,
    val permitTypeId: Int = 0,
    val permitDateMillis: Long? = null,
    val startTime: String = "",
    val endTime: String = "",
    val isSubmitting: Boolean = false,
    var submitSuccess: Boolean = false,
    val submitError: String? = null,
    val successMessage: String? = null
)

class CreatePermitViewModel(
    private val permitRepository: PermitRepository,
    private val authPreferences: AuthPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreatePermitUiState())
    val uiState: StateFlow<CreatePermitUiState> = _uiState.asStateFlow()

    val logginedUser = authPreferences.getLoggedInUser()

    init {
        fetchProjects()
    }

    private fun fetchProjects() {
        viewModelScope.launch {
            val request = org.example.project.data.model.PermitProjectListRequest(searchKey = "")
            when (val result = permitRepository.getPermitProjectList(request)) {
                is NetworkResult.Success -> {
                    // Give only the projects of the user where he is requestor (assuming userRole == 1)
                    val mappedProjects = result.data?.groups?.filter { it.userRole == 1 }?.map { it.toProject() } ?: emptyList()
                    _uiState.update { it.copy(projects = mappedProjects) }
                }
                is NetworkResult.Error -> {}
            }
        }
    }

    fun updateSelectedProject(project: org.example.project.data.model.Project?) {
        _uiState.update { it.copy(selectedProject = project, selectedUser = null, authorizedUsers = emptyList()) }
        if (project != null) {
            fetchAuthorizedUsers(project.groupId, project.groupCode ?: "")
        }
    }

    private fun fetchAuthorizedUsers(groupId: Int, groupCode: String) {
        viewModelScope.launch {
            // using designationType = 1 as default
            val request = org.example.project.data.model.PermitUserListRequest(
                groupId = groupId,
                groupCode = groupCode,
                designationType = 1
            )
            when (val result = permitRepository.getPermitUserList(request)) {
                is NetworkResult.Success -> {
                    val mappedUsers = result.data?.users?.map { it.toGroupUser() } ?: emptyList()
                    _uiState.update { it.copy(authorizedUsers = mappedUsers) }
                }
                is NetworkResult.Error -> {}
            }
        }
    }

    fun updateSelectedUser(user: org.example.project.data.model.GroupUser?) {
        _uiState.update { it.copy(selectedUser = user) }
    }

    fun fetchPermitContents(permitTypeId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, permitTypeId = permitTypeId) }
            val request = PermitContentRequest(id = permitTypeId)
            when (val result = permitRepository.getPermitContents(request)) {
                is NetworkResult.Success -> {
                    val data = result.data
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            certificateValidity = data?.certificateValidity ?: emptyList(),
                            generalConditions = data?.generalConditions ?: emptyList()
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, error = result.message)
                    }
                }
            }
        }
    }

    fun updateCertificateValidity(id: Int, value: String) {
        _uiState.update {
            it.copy(
                certificateValidityAnswers = it.certificateValidityAnswers.toMutableMap().apply {
                    put(id, value)
                }
            )
        }
    }

    fun updateGeneralCondition(id: Int, answer: String) {
        _uiState.update {
            it.copy(
                generalConditionAnswers = it.generalConditionAnswers.toMutableMap().apply {
                    put(id, answer)
                }
            )
        }
    }

    fun updateGeneralConditionRemark(id: Int, remark: String) {
        _uiState.update {
            it.copy(
                generalConditionRemarks = it.generalConditionRemarks.toMutableMap().apply {
                    put(id, remark)
                }
            )
        }
    }

    fun clearError() {
        _uiState.update {
            it.copy(error = null, submitError = null)
        }
    }

    fun updateSignatureUrl(url: String?) {
        if (url != null) {
            val now = kotlin.time.Clock.System.now()
            val localDateTime = now.toLocalDateTime(TimeZone.currentSystemDefault())
            val timeString =
                "${localDateTime.hour.toString().padStart(2, '0')}:${localDateTime.minute.toString().padStart(2, '0')}"
            _uiState.update {
                it.copy(
                    signatureUrl = url,
                    signatureDateMillis = now.toEpochMilliseconds(),
                    signatureTime = timeString
                ) 
            }
        } else {
            _uiState.update { 
                it.copy(
                    signatureUrl = null,
                    signatureDateMillis = null,
                    signatureTime = ""
                ) 
            }
        }
    }

    fun updatePermitDate(millis: Long?) {
        _uiState.update { it.copy(permitDateMillis = millis) }
    }

    fun updateStartTime(time: String) {
        _uiState.update { it.copy(startTime = time) }
    }

    fun updateEndTime(time: String) {
        _uiState.update { it.copy(endTime = time) }
    }

    fun submitPermit() {
        val currentState = _uiState.value
        val project = currentState.selectedProject
        val user = currentState.selectedUser
        val permitDate = currentState.permitDateMillis
        when {
            currentState.selectedProject == null -> {
                _uiState.update { it.copy(submitError = "Please select a project") }
                return
            }

            currentState.selectedUser == null -> {
                _uiState.update { it.copy(submitError = "Please select an authorized person") }
                return
            }

            currentState.permitDateMillis == null -> {
                _uiState.update { it.copy(submitError = "Please select a permit date") }
                return
            }

            currentState.startTime.isBlank() -> {
                _uiState.update { it.copy(submitError = "Please select a start time") }
                return
            }

            currentState.endTime.isBlank() -> {
                _uiState.update { it.copy(submitError = "Please select an end time") }
                return
            }

            currentState.generalConditions.any {
                currentState.generalConditionAnswers[it.id].isNullOrBlank()
            } -> {
                _uiState.update {
                    it.copy(submitError = "Please answer all general conditions")
                }
                return
            }

            currentState.signatureUrl.isNullOrBlank() -> {
                _uiState.update { it.copy(submitError = "Please add your signature") }
                return
            }
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, submitError = null, submitSuccess = false) }
            val request = PermitValiditySubmitRequest(
                permitTypeId = currentState.permitTypeId,
                requestDate = formatTimestamp(currentState.signatureDateMillis, "dd-MM-yyyy"),
                certificateDate = formatTimestamp(permitDate, "dd-MM-yyyy"),
                requestTime = currentState.signatureDateMillis?.let { millis ->
                    val utcDateTime = kotlinx.datetime.Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.UTC)
                    "${utcDateTime.hour.toString().padStart(2, '0')}:${utcDateTime.minute.toString().padStart(2, '0')}:${utcDateTime.second.toString().padStart(2, '0')}"
                } ?: "",

                validFrom = convertTo24HourFormat(currentState.startTime, true), // UTC time mapping may be required
                endTime = convertTo24HourFormat(currentState.endTime, true), // UTC time mapping may be required
                contractorName = "InstaResolv Private Limited",
                requestContractor = logginedUser?.name ?: "Unknown",
                signatureImageUrl = currentState.signatureUrl,
                images = emptyList(),
                project = PermitValidityProject(
                    groupId = project.groupId.toString(),
                    groupCode = project.groupCode ?: "",
                    groupName = project.groupName ?: "",
                    groupImage = project.groupImage ?: "",
                    userRole = 1,
                    isAdmin = project.isAdmin,
                    isSelected = true
                ),
                authorizedPersonId = user.userId,
                certificateValiditySections = currentState.certificateValidity.map {
                    PermitValiditySection(id = it.id, title = it.title ?: "", answer = currentState.certificateValidityAnswers[it.id] ?: "")
                },
                generalConditions = currentState.generalConditions.map { condition ->
                    val answerStr = currentState.generalConditionAnswers[condition.id]
                    val answerInt = when (answerStr?.lowercase()) {
                        "yes" -> 1
                        "no" -> 2
                        else -> 3 // NA or default
                    }
                    PermitValidityCondition(
                        id = condition.id,
                        title = condition.title ?: "",
                        answer = answerInt,
                        remarks = currentState.generalConditionRemarks[condition.id] ?: ""
                    )
                },
            )
            when (val result = permitRepository.submitPermitValidity(request)) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(isSubmitting = false, submitSuccess = true, successMessage = result.data.statusMessage) }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isSubmitting = false, submitError = result.message) }
                }
            }
        }
    }
}
