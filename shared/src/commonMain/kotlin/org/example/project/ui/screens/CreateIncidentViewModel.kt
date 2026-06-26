package org.example.project.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.data.model.GroupUser
import org.example.project.data.model.Project
import org.example.project.data.model.InjuredEmployee
import org.example.project.data.settings.AuthPreferences
import org.example.project.domain.repository.ProjectRepository
import org.example.project.network.NetworkResult
import org.example.project.ui.IncidentType
import org.example.project.data.model.EmployeeData
import org.example.project.data.model.AddIncidentRequest
import org.example.project.data.model.ImageDescriptionRequest
import org.example.project.data.model.InjuredEmployeeRequest
import org.example.project.domain.repository.IncidentRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.project.data.settings.formatDate

data class IncidentImage(
    val imageUrl: String? = null,
    val description: String = ""
)

data class CreateIncidentUiState(
    val isLoading: Boolean = false,
    val isLoadingUsers: Boolean = false,
    var error: String? = null,
    val selectedProject: Project? = null,
    val groupUsers: List<GroupUser> = emptyList(),
    val inspectedBy: GroupUser? = null,
    val reportedByName: String = "",
    val incidentDateMillis: Long? = null,
    val incidentTime: String = "",
    val location: String = "",
    val incidentTypes: List<Int> = emptyList(),
    val hasInjuredPerson: Boolean? = null,
    val injuredEmployees: List<InjuredEmployee> = emptyList(),
    val description: String = "",
    val immediateCorrections: String = "",
    val incidentImages: List<IncidentImage> = listOf(IncidentImage()),
    val isBulkUploadSheetVisible: Boolean = false,
    val bulkEmployees: List<EmployeeData> = emptyList(),
    val selectedBulkEmployees: Set<Int> = emptySet(),
    val bulkSearchQuery: String = "",
    val bulkPageNumber: Int = 1,
    val isBulkLoading: Boolean = false,
    val bulkHasMore: Boolean = true
)

class CreateIncidentViewModel(
    private val projectRepository: ProjectRepository,
    private val authPreferences: AuthPreferences,
    private val incidentRepository: IncidentRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateIncidentUiState())
    val uiState: StateFlow<CreateIncidentUiState> = _uiState.asStateFlow()

    init {
        val userName = authPreferences.getLoggedInUser()?.name ?: "Unknown User"
        _uiState.update { it.copy(reportedByName = userName) }
    }

    val logginedUser = authPreferences.getLoggedInUser()

    fun onProjectSelected(project: Project?) {
        _uiState.value = _uiState.value.copy(
            selectedProject = project,
            inspectedBy = null,
            groupUsers = emptyList()
        )
        
        if (project != null) {
            fetchGroupUsers(project.groupId ?: 0, project.groupCode ?: "")
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

    fun onInspectedBySelected(user: GroupUser) {
        _uiState.value = _uiState.value.copy(inspectedBy = user)
    }

    fun onDateSelected(millis: Long?) {
        _uiState.value = _uiState.value.copy(incidentDateMillis = millis)
    }

    fun onTimeChanged(time: String) {
        _uiState.value = _uiState.value.copy(incidentTime = time)
    }

    fun onLocationChanged(location: String) {
        _uiState.value = _uiState.value.copy(location = location)
    }

    fun onIncidentTypeToggled(typeId: Int) {
        val currentTypes = _uiState.value.incidentTypes.toMutableList()
        if (currentTypes.contains(typeId)) {
            currentTypes.remove(typeId)
        } else {
            currentTypes.add(typeId)
        }
        _uiState.value = _uiState.value.copy(incidentTypes = currentTypes)
    }

    fun onAddInjuredEmployee(employee: InjuredEmployee) {
        val currentEmployees = _uiState.value.injuredEmployees.toMutableList()
        currentEmployees.add(employee)
        _uiState.value = _uiState.value.copy(injuredEmployees = currentEmployees)
    }

    fun onRemoveInjuredEmployee(index: Int) {
        val currentEmployees = _uiState.value.injuredEmployees.toMutableList()
        if (index in currentEmployees.indices) {
            currentEmployees.removeAt(index)
            _uiState.value = _uiState.value.copy(injuredEmployees = currentEmployees)
        }
    }

    fun onInjuredPersonChanged(hasInjured: Boolean) {
        _uiState.value = _uiState.value.copy(hasInjuredPerson = hasInjured)
    }

    fun onDescriptionChanged(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }

    fun onCorrectionsChanged(corrections: String) {
        _uiState.value = _uiState.value.copy(immediateCorrections = corrections)
    }

    fun onImageDescriptionChange(index: Int, description: String) {
        val currentImages = _uiState.value.incidentImages.toMutableList()
        if (index in currentImages.indices) {
            currentImages[index] = currentImages[index].copy(description = description)
            _uiState.value = _uiState.value.copy(incidentImages = currentImages)
        }
    }

    fun onImageSelected(index: Int, url: String) {
        val currentImages = _uiState.value.incidentImages.toMutableList()
        if (index in currentImages.indices) {
            currentImages[index] = currentImages[index].copy(imageUrl = url)
            _uiState.value = _uiState.value.copy(incidentImages = currentImages)
        }
    }

    fun onImageRemoved(index: Int) {
        val currentImages = _uiState.value.incidentImages.toMutableList()
        if (index in currentImages.indices) {
            if (currentImages.size > 1) {
                currentImages.removeAt(index)
            } else {
                currentImages[index] = IncidentImage()
            }
            _uiState.value = _uiState.value.copy(incidentImages = currentImages)
        }
    }

    fun onAddImageSlot() {
        val currentImages = _uiState.value.incidentImages.toMutableList()
        if (currentImages.size < 6) {
            currentImages.add(IncidentImage())
            _uiState.value = _uiState.value.copy(incidentImages = currentImages)
        }
    }

    fun saveIncident(
        isDraft: Boolean,
        onSuccess: () -> Unit
    ) {
        val state = _uiState.value
        if (state.incidentDateMillis == null) {
            _uiState.value = state.copy(error = "Incident Date is required")
            return
        }
        if (state.incidentTime.isBlank()) {
            _uiState.value = state.copy(error = "Incident Time is required")
            return
        }
        if (state.incidentTypes.isEmpty()) {
            _uiState.value = state.copy(error = "At least one Incident Type is required")
            return
        }
        if (state.hasInjuredPerson == null) {
            _uiState.value = state.copy(error = "Injured Person Details is required")
            return
        }

        if (state.hasInjuredPerson == true && state.injuredEmployees.isEmpty()) {
            _uiState.value = state.copy(error = "Injured Person Details is required")
            return
        }

        if (state.selectedProject == null) {
            _uiState.update { it.copy(error = "Please select a project") }
            return
        }

        val incidentTime = try {
            val cleanTime = state.incidentTime.replace(" : ", ":")
            val parts = cleanTime.split(":", " ")
            if (parts.size >= 3) {
                var h = parts[0].toInt()
                val m = parts[1]
                val amPm = parts[2]
                if (amPm.equals("PM", ignoreCase = true) && h < 12) h += 12
                if (amPm.equals("AM", ignoreCase = true) && h == 12) h = 0
                "${h.toString().padStart(2, '0')}:$m:00"
            } else {
                state.incidentTime
            }
        } catch (e: Exception) {
            state.incidentTime
        }

        val dateInstantStr = state.incidentDateMillis?.let {
            kotlinx.datetime.Instant.fromEpochMilliseconds(it).toString()
        } ?: ""

        val dateOnlyStr = if (dateInstantStr.isNotEmpty()) {
            formatDate(
                dateInstantStr,
                inputPattern = "",
                outputPattern = "yyyy-MM-dd"
            )
        } else ""

        val incidentDate = if (dateOnlyStr.isNotEmpty() && incidentTime.isNotEmpty()) {
            "$dateOnlyStr $incidentTime"
        } else ""

        val formattedCreatedAt = formatDate(
            kotlin.time.Clock.System.now().toString(),
            inputPattern = "",
            outputPattern = "yyyy-MM-dd HH:mm:ss"
        )



        val request = AddIncidentRequest(
            corrections = state.immediateCorrections,
            facilitiesId = state.selectedProject?.groupId?.toString() ?: "",
            incidentType = state.incidentTypes,
            injuredEmployees = state.injuredEmployees.map {
                InjuredEmployeeRequest(
                    profession = it.profession ?: "",
                    id = -1,
                    employeeName = it.employeeName ?: "",
                    companyName = it.companyName ?: "",
                    employeeCode = it.employeeCode ?: ""
                )
            },
            incidentTime = incidentTime,
            createdAt = formattedCreatedAt,
            incidentLocation = state.location,
            reportedBy = state.reportedByName,
            incidentDate = incidentDate,
            saveAsDraft = isDraft,
            images = state.incidentImages.mapNotNull {
                if (it.imageUrl != null) {
                    ImageDescriptionRequest(
                        isAiGeneratedDescription = false,
                        image = it.imageUrl,
                        description = it.description,
                        imageCount = 1
                    )
                } else null
            },
            description = state.description
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = incidentRepository.addIncident(request)
            
            when (result) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    onSuccess()
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message ?: "Failed to publish incident") }
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun setError(error: String) {
        _uiState.value = _uiState.value.copy(error = error)
    }

    fun openBulkUploadSheet() {
        _uiState.update { it.copy(isBulkUploadSheetVisible = true) }
        fetchBulkEmployees(isLoadMore = false)
    }

    fun closeBulkUploadSheet() {
        _uiState.update { it.copy(isBulkUploadSheetVisible = false, bulkSearchQuery = "", selectedBulkEmployees = emptySet(), bulkEmployees = emptyList()) }
    }

    fun onBulkSearchQueryChanged(query: String) {
        _uiState.update { it.copy(bulkSearchQuery = query) }
        fetchBulkEmployees(isLoadMore = false)
    }

    fun toggleBulkEmployeeSelection(employeeId: Int) {
        val currentSet = _uiState.value.selectedBulkEmployees.toMutableSet()
        if (currentSet.contains(employeeId)) {
            currentSet.remove(employeeId)
        } else {
            currentSet.add(employeeId)
        }
        _uiState.update { it.copy(selectedBulkEmployees = currentSet) }
    }

    fun selectAllBulkEmployees() {
        val allIds = _uiState.value.bulkEmployees.map { it.id }.toSet()
        _uiState.update { it.copy(selectedBulkEmployees = allIds) }
    }

    fun fetchBulkEmployees(isLoadMore: Boolean = false) {
        val state = _uiState.value
        if (state.selectedProject == null || state.isBulkLoading || (!state.bulkHasMore && isLoadMore)) return

        viewModelScope.launch {
            val page = if (isLoadMore) state.bulkPageNumber + 1 else 1
            _uiState.update { it.copy(isBulkLoading = true) }

            val result = projectRepository.getEmployeeList(
                groupId = state.selectedProject.groupId.toString(),
                pageNumber = page,
                searchKey = state.bulkSearchQuery
            )

            when (result) {
                is NetworkResult.Success -> {
                    val newEmployees = result.data
                    val currentList = if (isLoadMore) state.bulkEmployees else emptyList()
                    _uiState.update {
                        it.copy(
                            isBulkLoading = false,
                            bulkEmployees = currentList + newEmployees,
                            bulkPageNumber = page,
                            bulkHasMore = newEmployees.isNotEmpty()
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isBulkLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    fun onAddBulkEmployees() {
        val state = _uiState.value
        val selectedEmployees = state.bulkEmployees.filter { state.selectedBulkEmployees.contains(it.id) }
        
        val newInjuredEmployees = selectedEmployees.map {
            InjuredEmployee(
                employeeCode = it.employeeCode ?: "",
                employeeName = it.employeeName ?: "",
                companyName = it.companyName ?: "",
                profession = it.profession ?: ""
            )
        }
        
        val currentList = state.injuredEmployees.toMutableList()
        currentList.addAll(newInjuredEmployees)
        
        _uiState.update {
            it.copy(
                injuredEmployees = currentList,
                isBulkUploadSheetVisible = false,
                bulkSearchQuery = "",
                selectedBulkEmployees = emptySet(),
                bulkEmployees = emptyList()
            )
        }
    }
}
