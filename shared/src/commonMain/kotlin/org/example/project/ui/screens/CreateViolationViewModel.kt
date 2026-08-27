package org.example.project.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.example.project.data.model.CreateViolationRequest
import org.example.project.data.model.ImageDescriptionRequest
import org.example.project.data.model.Project
import org.example.project.data.model.GroupUser
import org.example.project.data.model.GroupUserRequest
import org.example.project.data.model.LocalViolationImage
import org.example.project.data.model.CreateViolationDraftRequest
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.TimeZone
import kotlin.time.Clock
import org.example.project.data.settings.AuthPreferences
import org.example.project.data.repository.ViolationDraftRepository
import org.example.project.domain.repository.ProjectRepository
import org.example.project.domain.repository.ViolationRepository
import org.example.project.network.NetworkResult
import org.example.project.ui.components.imagepicker.ImagePickerUiState
import kotlin.random.Random

data class CreateViolationUiState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null,
    
    val draftId: Long? = null,
    val selectedProject: Project? = null,
    val groupUsers: List<GroupUser> = emptyList(),
    val reportedBy: GroupUser? = null,
    val employeeName: String = "",
    val employeeId: String = "",
    val violationDate: String = "",
    val violationDateMillis: Long? = null,
    val location: String = "",
    val description: String = "",
    
    val images: List<LocalViolationImage> = emptyList()
)

class CreateViolationViewModel(
    private val preferences: AuthPreferences,
    private val repository: ViolationRepository,
    private val projectRepository: ProjectRepository,
    private val violationDraftRepository: ViolationDraftRepository
) : ViewModel() {

    val user = preferences.getLoggedInUser()
    private val _uiState = MutableStateFlow(CreateViolationUiState())
    val uiState: StateFlow<CreateViolationUiState> = _uiState.asStateFlow()

    fun updateSelectedProject(project: Project) {
        _uiState.update { it.copy(selectedProject = project) }
        fetchGroupUsers(project.groupId, project.groupCode ?: "")
    }

    private fun fetchGroupUsers(groupId: Int, groupCode: String) {
        viewModelScope.launch {
            when (val result = projectRepository.getGroupUsers(groupId, groupCode)) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(groupUsers = result.data?.users ?: emptyList()) }
                }
                else -> {
                    _uiState.update { it.copy(groupUsers = emptyList()) }
                }
            }
        }
    }

    fun updateReportedBy(user: GroupUser) {
        _uiState.update { it.copy(reportedBy = user) }
    }

    fun updateEmployeeName(name: String) {
        _uiState.update { it.copy(employeeName = name) }
    }
    
    fun updateEmployeeId(id: String) {
        _uiState.update { it.copy(employeeId = id) }
    }

    fun updateViolationDate(date: String, millis: Long) {
        _uiState.update { it.copy(violationDate = date, violationDateMillis = millis) }
    }

    fun updateLocation(loc: String) {
        _uiState.update { it.copy(location = loc) }
    }

    fun updateDescription(desc: String) {
        _uiState.update { it.copy(description = desc) }
    }

    fun addImageBlock() {
        val newList = _uiState.value.images.toMutableList()
        newList.add(LocalViolationImage(id = Random.nextInt()))
        _uiState.update { it.copy(images = newList) }
    }

    fun updateImageBlock(id: Int, url: String) {
        val newList = _uiState.value.images.map {
            if (it.id == id) it.copy(imageUrl = url) else it
        }
        _uiState.update { it.copy(images = newList) }
    }

    fun updateImageDescription(id: Int, desc: String) {
        val newList = _uiState.value.images.map {
            if (it.id == id) it.copy(description = desc) else it
        }
        _uiState.update { it.copy(images = newList) }
    }

    fun removeImageBlock(id: Int) {
        val newList = _uiState.value.images.filter { it.id != id }
        _uiState.update { it.copy(images = newList) }
    }

    fun saveViolation(isDraft: Boolean, onSuccess: () -> Unit) {
        val state = _uiState.value
        
        if (!isDraft) {
            if (state.employeeName.isBlank()) {
                _uiState.update { it.copy(error = "Employee Name is required") }
                return
            }
            if (state.employeeId.isBlank()) {
                _uiState.update { it.copy(error = "Employee ID is required") }
                return
            }
            if (state.violationDate.isBlank()) {
                _uiState.update { it.copy(error = "Violation Date is required") }
                return
            }
        }
        
        val request = CreateViolationRequest(
            facilitiesId = state.selectedProject?.groupId?.toString(),
            employeeName = state.employeeName,
            employeeId = state.employeeId,
            violationDate = state.violationDate,
            location = state.location.takeIf { it.isNotBlank() },
            description = state.description.takeIf { it.isNotBlank() },
            images = state.images.filter { !it.imageUrl.isNullOrEmpty() }.map {
                ImageDescriptionRequest(
                    image = it.imageUrl ?: "",
                    description = it.description,
                    isAiGeneratedDescription = false
                )
            },
            reportedBy = user?.name ?: "",
            saveAsDraft = isDraft
        )
        _uiState.update { it.copy(isLoading = true, error = null, success = false) }
        viewModelScope.launch {
            when (val result = repository.createViolation(request)) {
                is NetworkResult.Success -> {
                    val data = result.data
                    if (data.violationId != null) {
                        _uiState.update { it.copy(isLoading = false, success = true) }
                        if (state.draftId != null) {
                            deleteDraft(state.draftId)
                        }
                        onSuccess()
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = data.statusMessage ?: "Failed to create violation"
                            )
                        }
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

    fun saveLocalDraft(onSuccess: () -> Unit) {
        val state = _uiState.value
        val hasValue = state.selectedProject != null ||
                       state.employeeName.isNotBlank() ||
                       state.employeeId.isNotBlank() ||
                       state.violationDate.isNotBlank() ||
                       state.location.isNotBlank() ||
                       state.description.isNotBlank() ||
                       state.images.any { !it.imageUrl.isNullOrBlank() }
        
        if (!hasValue) {
            _uiState.update { it.copy(error = "At least one field must have a value to save as draft") }
            return
        }

        val request = CreateViolationDraftRequest(
            draftId = state.draftId,
            userId = user?.userId ?: 0,
            facilitiesId = state.selectedProject?.groupId?.toString(),
            facility = state.selectedProject,
            employeeName = state.employeeName,
            employeeId = state.employeeId,
            violationDate = state.violationDate,
            violationDateMillis = state.violationDateMillis,
            location = state.location,
            description = state.description,
            images = state.images,
            reportedBy = user?.name ?: "",
            createdAt = run {
                val localDateTime = Clock.System.now().toLocalDateTime(kotlinx.datetime.TimeZone.UTC)
                val year = localDateTime.year
                val month = localDateTime.monthNumber.toString().padStart(2, '0')
                val day = localDateTime.dayOfMonth.toString().padStart(2, '0')
                val hour = localDateTime.hour.toString().padStart(2, '0')
                val minute = localDateTime.minute.toString().padStart(2, '0')
                val second = localDateTime.second.toString().padStart(2, '0')
                "$year-$month-$day $hour:$minute:$second"
            }
        )
        viewModelScope.launch {
            violationDraftRepository.saveDraft(request)
            onSuccess()
        }
    }

    fun getDraftById(draftId: Long): org.example.project.shared.db.ViolationDraft? {
        return violationDraftRepository.getDraftById(draftId)
    }

    fun deleteDraft(draftId: Long) {
        viewModelScope.launch {
            violationDraftRepository.deleteDraft(draftId)
        }
    }

    fun restoreDraftData(draft: org.example.project.shared.db.ViolationDraft) {
        val project = draft.facilityJson?.let {
            try {
                Json.decodeFromString<Project>(it)
            } catch (e: Exception) {
                null
            }
        }
        val listImages = draft.imagesJson.mapNotNull {
            try {
                Json.decodeFromString<LocalViolationImage>(it)
            } catch (e: Exception) {
                null
            }
        }
        _uiState.update { state ->
            state.copy(
                draftId = draft.id,
                selectedProject = project,
                employeeName = draft.employeeName,
                employeeId = draft.employeeId,
                violationDate = draft.violationDate,
                violationDateMillis = draft.violationDateMillis,
                location = draft.location ?: "",
                description = draft.description ?: "",
                images = listImages
            )
        }
        project?.let { fetchGroupUsers(it.groupId, it.groupCode ?: "") }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
    fun resetState() {
        _uiState.update { CreateViolationUiState() }
    }
}
