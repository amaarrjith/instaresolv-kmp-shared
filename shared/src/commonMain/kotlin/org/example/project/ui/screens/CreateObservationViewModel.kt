package org.example.project.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.data.model.CreateObservationDraftRequest
import org.example.project.data.model.GroupUser
import org.example.project.data.model.Project
import org.example.project.data.settings.AuthPreferences
import org.example.project.domain.repository.ProjectRepository
import org.example.project.network.NetworkResult
import org.example.project.data.repository.ObservationDraftRepository
import kotlin.time.Clock

data class ObservationImage(
    val imageUrl: String? = null,
    val description: String = ""
)

data class CreateObservationUiState(
    val isLoading: Boolean = false,
    val isLoadingUsers: Boolean = false,
    val error: String? = null,
    val selectedProject: Project? = null,
    val groupUsers: List<GroupUser> = emptyList(),
    val manualResponsibleName: String = "",
    val manualResponsibleEmail: String = "",
    val selectedResponsiblePerson: GroupUser? = null,
    val selectedNotifyPerson: GroupUser? = null,
    val observationImages: List<ObservationImage> = listOf(ObservationImage()),
    val audioUrl: String? = null
)

class CreateObservationViewModel(
    private val projectRepository: ProjectRepository,
    private val observationRepository: org.example.project.domain.repository.ObservationRepository,
    private val authPreferences: AuthPreferences,
    private val observationDraftRepository: ObservationDraftRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateObservationUiState())
    val uiState: StateFlow<CreateObservationUiState> = _uiState.asStateFlow()

    val logginedUser = authPreferences.getLoggedInUser()

    fun onProjectSelected(project: Project?) {
        _uiState.value = _uiState.value.copy(
            selectedProject = project,
            selectedResponsiblePerson = null,
            selectedNotifyPerson = null,
            groupUsers = emptyList()
        )
        
        if (project != null) {
            fetchGroupUsers(project.groupId ?: 0, project.groupCode ?: "")
        }
    }
    fun onAudioUrlProcessed(url: String?) {
        println("Audio URL: $url")
        _uiState.value = _uiState.value.copy(audioUrl = url)
    }
    fun onManualResponsibleNameChange(name: String) {
        _uiState.value = _uiState.value.copy(manualResponsibleName = name)
    }

    fun onManualResponsibleEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(manualResponsibleEmail = email)
    }

    fun onResponsiblePersonSelected(user: GroupUser) {
        _uiState.value = _uiState.value.copy(selectedResponsiblePerson = user)
    }

    fun onNotifyPersonSelected(user: GroupUser) {
        _uiState.value = _uiState.value.copy(selectedNotifyPerson = user)
    }

    fun onImageDescriptionChange(index: Int, description: String) {
        val currentImages = _uiState.value.observationImages.toMutableList()
        if (index in currentImages.indices) {
            currentImages[index] = currentImages[index].copy(description = description)
            _uiState.value = _uiState.value.copy(observationImages = currentImages)
        }
    }

    fun onImageSelected(index: Int, url: String) {
        val currentImages = _uiState.value.observationImages.toMutableList()
        if (index in currentImages.indices) {
            currentImages[index] = currentImages[index].copy(imageUrl = url)
            _uiState.value = _uiState.value.copy(observationImages = currentImages)
        }
    }

    fun onImageRemoved(index: Int) {
        val currentImages = _uiState.value.observationImages.toMutableList()
        if (index in currentImages.indices) {
            if (currentImages.size > 1) {
                currentImages.removeAt(index)
            } else {
                currentImages[index] = ObservationImage()
            }
            _uiState.value = _uiState.value.copy(observationImages = currentImages)
        }
    }

    fun onAddImageSlot() {
        val currentImages = _uiState.value.observationImages.toMutableList()
        if (currentImages.size < 6) {
            currentImages.add(ObservationImage())
            _uiState.value = _uiState.value.copy(observationImages = currentImages)
        }
    }

    private fun fetchGroupUsers(groupId: Int, groupCode: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingUsers = true, error = null)
            val result = projectRepository.getGroupUsers(groupId, groupCode)
            
            when (result) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoadingUsers = false,
                        groupUsers = result.data?.users ?: emptyList()
                    )
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoadingUsers = false,
                        error = result.message ?: "Failed to fetch users"
                    )
                }
            }
        }
    }

    fun saveObservation(
        draftId: Long? = null,
        title: String,
        location: String,
        description: String,
        isDraft: Boolean,
        onSuccess: () -> Unit
    ) {
        val state = _uiState.value
        if (isDraft) {
            val hasValue = title.isNotBlank() ||
                           location.isNotBlank() ||
                           description.isNotBlank() ||
                           state.selectedProject != null ||
                           state.observationImages.any { !it.imageUrl.isNullOrBlank() } ||
                           !state.audioUrl.isNullOrBlank() ||
                           state.selectedResponsiblePerson != null ||
                           state.selectedNotifyPerson != null ||
                           state.manualResponsibleName.isNotBlank() ||
                           state.manualResponsibleEmail.isNotBlank()
            if (!hasValue) {
                _uiState.value = state.copy(error = "At least one field must have a value to save as draft")
                return
            }
        } else {
            if (title.isBlank()) {
                _uiState.value = state.copy(error = "Title is required")
                return
            }
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = null)

            val imageRequests = state.observationImages.mapIndexed { index, img ->
                org.example.project.data.model.ImageDescriptionRequest(
                    image = img.imageUrl ?: "",
                    description = img.description,
                    imageCount = index + 1
                )
            }.filter { it.image.isNotBlank() }

            if (isDraft) {
                val request = CreateObservationDraftRequest(
                    draftId = draftId,
                    userId = authPreferences.getLoggedInUser()?.userId ?: -1,
                    location = location,
                    imageDescription = imageRequests,
                    groupSpecified = if (state.selectedProject != null) 1 else 0,
                    group = state.selectedProject?.let {
                        org.example.project.data.model.ObservationGroup(
                            groupId = it.groupId.toString(),
                            groupCode = it.groupCode,
                            groupImage = it.groupImage,
                            groupName = it.groupName
                        )
                    },
                    reportedBy = logginedUser?.name ?: "",
                    customResponsiblePerson = org.example.project.data.model.CustomResponsiblePersonRequest(
                        name = if (state.selectedProject == null) state.manualResponsibleName else ""
                    ),
                    notificationTo = listOfNotNull(state.selectedNotifyPerson),
                    description = description,
                    observationId = -1,
                    responsiblePerson = state.selectedResponsiblePerson,
                    responsiblePersonName = if (state.selectedProject != null) (state.selectedResponsiblePerson?.name ?: "") else state.manualResponsibleName,
                    observationTitle = title,
                    responsiblePersonEmail = if (state.selectedProject != null) (state.selectedResponsiblePerson?.email ?: "") else state.manualResponsibleEmail,
                    audioLink = state.audioUrl,
                    createdAt = Clock.System.now().toString()
                )
                try {
                    observationDraftRepository.saveDraft(request)
                    _uiState.value = _uiState.value.copy(isLoading = false, error = null)
                    onSuccess()
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to save draft"
                    )
                }
                return@launch
            } else {
                val request = org.example.project.data.model.CreateObservationRequest(
                    location = location,
                    imageDescription = imageRequests,
                    groupSpecified = if (state.selectedProject != null) 1 else 0,
                    groupId = state.selectedProject?.groupId ?: -1,
                    reportedBy = logginedUser?.name ?: "",
                    customResponsiblePerson = org.example.project.data.model.CustomResponsiblePersonRequest(
                        name = if (state.selectedProject == null) state.manualResponsibleName else ""
                    ),
                    notificationTo = listOfNotNull(state.selectedNotifyPerson?.userId),
                    saveAsDraft = isDraft,
                    description = description,
                    observationId = -1,
                    responsiblePerson = state.selectedResponsiblePerson?.userId ?: -1,
                    responsiblePersonName = if (state.selectedProject != null) (state.selectedResponsiblePerson?.name ?: "") else state.manualResponsibleName,
                    observationTitle = title,
                    responsiblePersonEmail = if (state.selectedProject != null) (state.selectedResponsiblePerson?.email ?: "") else state.manualResponsibleEmail,
                    audioLink = state.audioUrl
                )

                val result = observationRepository.createObservation(request)

                when (result) {
                    is NetworkResult.Success -> {
                        if (draftId != null) {
                            deleteDraftById(draftId)
                        }
                        _uiState.value = _uiState.value.copy(isLoading = false, error = null)
                        onSuccess()
                    }

                    is NetworkResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.message ?: "Failed to save observation"
                        )
                    }
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun getDraftById(draftId: Long): org.example.project.shared.db.ObservationDraft? {
        return observationDraftRepository.getDraftById(draftId)
    }

    fun deleteDraftById(draftId: Long) {
        observationDraftRepository.deleteDraft(draftId)
    }

    fun restoreDraftData(draft: org.example.project.shared.db.ObservationDraft) {
        if (!draft.groupJson.isNullOrEmpty()) {
            try {
                val group = kotlinx.serialization.json.Json.decodeFromString<org.example.project.data.model.ObservationGroup>(draft.groupJson)
                onProjectSelected(Project(
                    groupId = group.groupId?.toIntOrNull() ?: 0,
                    groupName = group.groupName,
                    groupImage = group.groupImage,
                    groupCode = group.groupCode,
                    isAdmin = false
                ))
            } catch (e: Exception) {}
        }
        
        if (draft.customResponsiblePersonName.isNotEmpty()) {
            onManualResponsibleNameChange(draft.customResponsiblePersonName)
        }

        if (draft.responsiblePersonId > 0 && draft.responsiblePersonName.isNotEmpty()) {
            val user = GroupUser(
                userId = draft.responsiblePersonId.toInt(),
                name = draft.responsiblePersonName,
                email = draft.responsiblePersonEmail,
                image = "",
                role = 0
            )
            onResponsiblePersonSelected(user)
        }
        
        if (draft.notificationToJson.isNotEmpty()) {
            val notifyStr = draft.notificationToJson.firstOrNull()
            if (notifyStr != null) {
                try {
                    val user = kotlinx.serialization.json.Json.decodeFromString<GroupUser>(notifyStr)
                    onNotifyPersonSelected(user)
                } catch (e: Exception) {
                    // Fallback in case of old draft format (just ID)
                    val notifyId = notifyStr.toIntOrNull()
                    if (notifyId != null) {
                        onNotifyPersonSelected(GroupUser(
                            userId = notifyId, 
                            name = "Selected User", 
                            email = "",
                            image = "",
                            role = 0
                        ))
                    }
                }
            }
        }

        if (draft.imageDescriptionsJson.isNotEmpty()) {
            val images = draft.imageDescriptionsJson.mapNotNull { 
                try {
                    val req = kotlinx.serialization.json.Json.decodeFromString<org.example.project.data.model.ImageDescriptionRequest>(it)
                    ObservationImage(imageUrl = req.image, description = req.description)
                } catch (e: Exception) { null }
            }
            if (images.isNotEmpty()) {
                _uiState.value = _uiState.value.copy(observationImages = images)
            }
        }
        
        if (!draft.audioLink.isNullOrEmpty()) {
            onAudioUrlProcessed(draft.audioLink)
        }
    }
}
