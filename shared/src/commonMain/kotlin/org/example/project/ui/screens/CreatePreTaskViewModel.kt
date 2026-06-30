package org.example.project.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.project.data.model.GroupUser
import org.example.project.data.model.PreTaskContentData
import org.example.project.data.model.PreTaskContentRequest
import org.example.project.data.model.PreTaskQuestionData
import org.example.project.data.model.Project
import org.example.project.data.model.EmployeeData
import org.example.project.data.settings.AuthPreferences
import org.example.project.domain.repository.PreTaskRepository
import org.example.project.domain.repository.ProjectRepository
import org.example.project.network.NetworkResult

data class AttendeeItem(
    val employeeCode: String = "",
    val employeeName: String = "",
    val companyName: String = "",
    val profession: String = ""
)

data class EvidenceImage(
    val imagePath: String = "",
    val description: String = ""
)

data class CustomQuestion(
    val id: Int,
    val title: String = "",
    val answer: String = "" // "Yes", "No", "NA"
)

data class CreatePreTaskUiState(
    val isLoading: Boolean = false,
    val isPublishing: Boolean = false,
    val error: String? = null,
    val publishSuccess: Boolean = false,
    
    // Form fields
    val selectedProject: Project? = null,
    val taskTitle: String = "",
    val reportedBy: GroupUser? = null,
    val dateMillis: Long? = null,
    val startTime: String = "",
    val endTime: String = "",
    val msraReference: String = "",
    val permitReference: String = "",
    
    // Step by step account
    val stepByStepAccount: String = "",
    
    // Topics of Discussion
    val contents: List<PreTaskContentData> = emptyList(),
    val questions: List<PreTaskQuestionData> = emptyList(),
    val questionAnswers: Map<Int, String> = emptyMap(), // Question ID to Answer String
    val customQuestions: List<CustomQuestion> = listOf(CustomQuestion(1)), // OTHERS section
    
    // Attendees & Evidence
    val attendees: List<AttendeeItem> = listOf(AttendeeItem()),
    val evidences: List<EvidenceImage> = listOf(EvidenceImage()),
    
    // Notification Users
    val groupUsers: List<GroupUser> = emptyList(),
    val selectedNotifyPerson: GroupUser? = null,
    
    // Bulk Employee Upload
    val isBulkUploadSheetVisible: Boolean = false,
    val bulkEmployees: List<EmployeeData> = emptyList(),
    val selectedBulkEmployees: Set<Int> = emptySet(),
    val bulkSearchQuery: String = "",
    val bulkPageNumber: Int = 1,
    val isBulkLoading: Boolean = false,
    val bulkHasMore: Boolean = true
)

class CreatePreTaskViewModel(
    private val projectRepository: ProjectRepository,
    private val preTaskRepository: PreTaskRepository,
    private val authPreferences: AuthPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreatePreTaskUiState())
    val uiState: StateFlow<CreatePreTaskUiState> = _uiState.asStateFlow()

    val logginedUser = authPreferences.getLoggedInUser()

    init {
        // Pre-fill reportedBy if possible
        logginedUser?.let { user ->
            _uiState.update { 
                it.copy(
                    reportedBy = GroupUser(
                        userId = user.userId ?: 0,
                        name = user.name ?: "",
                        email = user.email ?: "",
                        image = user.profileImage ?: "",
                        role = user.userType ?: 0
                    )
                ) 
            }
        }
        fetchTopics()
    }

    private fun fetchTopics() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val request = PreTaskContentRequest(
                contentUpdatedTime = "",
                questionUpdatedTime = ""
            )
            when (val result = preTaskRepository.getPreTaskContent(request)) {
                is NetworkResult.Success -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            contents = result.data.contents?.sortedBy { it.order } ?: emptyList(),
                            questions = result.data.questions ?: emptyList()
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = result.message ?: "Failed to load topics"
                        )
                    }
                }
            }
        }
    }

    fun onProjectSelected(project: Project?) {
        _uiState.update { it.copy(selectedProject = project) }
        project?.let { fetchGroupUsers(it.groupId.toString(), it.groupCode ?: "") }
    }

    private fun fetchGroupUsers(groupId: String, groupCode: String) {
        viewModelScope.launch {
            val result = projectRepository.getGroupUsers(groupId.toIntOrNull() ?: 0, groupCode)
            if (result is NetworkResult.Success) {
                _uiState.update { it.copy(groupUsers = result.data?.users ?: emptyList()) }
            }
        }
    }

    fun onReportedBySelected(user: GroupUser?) {
        _uiState.update { it.copy(reportedBy = user) }
    }
    
    fun onNotifyPersonSelected(user: GroupUser?) {
        _uiState.update { it.copy(selectedNotifyPerson = user) }
    }
    
    fun onTaskTitleChanged(title: String) {
        _uiState.update { it.copy(taskTitle = title) }
    }
    
    fun onDateSelected(millis: Long) {
        _uiState.update { it.copy(dateMillis = millis) }
    }

    fun onStartTimeChanged(time: String) {
        _uiState.update { it.copy(startTime = time) }
    }

    fun onEndTimeChanged(time: String) {
        _uiState.update { it.copy(endTime = time) }
    }
    
    fun onMsraChanged(msra: String) {
        _uiState.update { it.copy(msraReference = msra) }
    }

    fun onPermitChanged(permit: String) {
        _uiState.update { it.copy(permitReference = permit) }
    }
    
    fun onStepByStepAccountChanged(account: String) {
        _uiState.update { it.copy(stepByStepAccount = account) }
    }

    fun onQuestionAnswered(questionId: Int, answer: String) {
        _uiState.update { 
            val newAnswers = it.questionAnswers.toMutableMap()
            newAnswers[questionId] = answer
            it.copy(questionAnswers = newAnswers)
        }
    }

    // Custom Questions (OTHERS)
    fun addCustomQuestion() {
        _uiState.update {
            val newId = (it.customQuestions.maxOfOrNull { q -> q.id } ?: 0) + 1
            it.copy(customQuestions = it.customQuestions + CustomQuestion(id = newId))
        }
    }

    fun updateCustomQuestionTitle(id: Int, title: String) {
        _uiState.update {
            val updated = it.customQuestions.map { q -> if (q.id == id) q.copy(title = title) else q }
            it.copy(customQuestions = updated)
        }
    }

    fun updateCustomQuestionAnswer(id: Int, answer: String) {
        _uiState.update {
            val updated = it.customQuestions.map { q -> if (q.id == id) q.copy(answer = answer) else q }
            it.copy(customQuestions = updated)
        }
    }

    // Attendees
    fun addAttendee(attendee: AttendeeItem = AttendeeItem()) {
        _uiState.update { it.copy(attendees = it.attendees + attendee) }
    }

    fun updateAttendee(index: Int, attendee: AttendeeItem) {
        _uiState.update {
            val updated = it.attendees.toMutableList()
            if (index in updated.indices) {
                updated[index] = attendee
            }
            it.copy(attendees = updated)
        }
    }

    fun onRemoveAttendee(index: Int) {
        _uiState.update {
            val updated = it.attendees.toMutableList()
            if (index in updated.indices) {
                updated.removeAt(index)
            }
            it.copy(attendees = updated)
        }
    }

    // Evidences
    fun addEvidence() {
        _uiState.update { it.copy(evidences = it.evidences + EvidenceImage()) }
    }

    fun updateEvidenceImage(index: Int, imagePath: String) {
        _uiState.update {
            val updated = it.evidences.toMutableList()
            if (index in updated.indices) {
                updated[index] = updated[index].copy(imagePath = imagePath)
            }
            it.copy(evidences = updated)
        }
    }

    fun updateEvidenceDescription(index: Int, description: String) {
        _uiState.update {
            val updated = it.evidences.toMutableList()
            if (index in updated.indices) {
                updated[index] = updated[index].copy(description = description)
            }
            it.copy(evidences = updated)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun clearSuccess() {
        _uiState.update { it.copy(publishSuccess = false) }
    }

    fun setError(error: String) {
        _uiState.update { it.copy(error = error) }
    }

    // Bulk Employee Upload Functions
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
                    val newEmployees = result.data ?: emptyList()
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
        
        val newAttendees = selectedEmployees.map {
            AttendeeItem(
                employeeCode = it.employeeCode ?: "",
                employeeName = it.employeeName ?: "",
                companyName = it.companyName ?: "",
                profession = it.profession ?: ""
            )
        }
        
        val currentList = state.attendees.filter { it.employeeCode.isNotBlank() || it.employeeName.isNotBlank() }.toMutableList()
        currentList.addAll(newAttendees)
        // Ensure at least one empty item if empty? No, CreatePreTask might not need an empty item because we use AddEmployeeBlock now.
        
        _uiState.update {
            it.copy(
                attendees = currentList,
                isBulkUploadSheetVisible = false,
                bulkSearchQuery = "",
                selectedBulkEmployees = emptySet(),
                bulkEmployees = emptyList()
            )
        }
    }

    // Publish
    fun publishPreTask(isDraft: Boolean = false) {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isPublishing = true, error = null) }
            
            if (state.selectedProject == null) {
                _uiState.update { it.copy(isPublishing = false, error = "Please select a Project") }
                return@launch
            }
            if (state.taskTitle.isBlank()) {
                _uiState.update { it.copy(isPublishing = false, error = "Task Title is required") }
                return@launch
            }
            
            val instant = Instant.fromEpochMilliseconds(state.dateMillis ?: 0L)
            val localDate = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
            val formattedDate = "${localDate.dayOfMonth.toString().padStart(2, '0')}-${localDate.monthNumber.toString().padStart(2, '0')}-${localDate.year}"
            val currentInstantEpoch = kotlin.time.Clock.System.now().toEpochMilliseconds()
            val currentInstant = Instant.fromEpochMilliseconds(currentInstantEpoch)
            val currentDt = currentInstant.toLocalDateTime(TimeZone.currentSystemDefault())
            val currentCreatedAt = "${currentDt.year}-${currentDt.monthNumber.toString().padStart(2, '0')}-${currentDt.dayOfMonth.toString().padStart(2, '0')} ${currentDt.hour.toString().padStart(2, '0')}:${currentDt.minute.toString().padStart(2, '0')}:${currentDt.second.toString().padStart(2, '0')}"
            
            val mappedContents = state.contents.map {
                org.example.project.data.model.CreatePreTaskContent(
                    id = it.id,
                    title = it.title ?: ""
                )
            }
            
            val mappedQuestions = state.questions.map {
                val ansStr = state.questionAnswers[it.id] ?: ""
                val ansInt = when (ansStr) {
                    "Yes" -> 1
                    "No" -> 2
                    "Not Applicable" -> 3
                    else -> 0
                }
                org.example.project.data.model.CreatePreTaskQuestion(
                    id = it.id,
                    contentId = it.contentId ?: 0,
                    title = it.title ?: "",
                    imageUrl = it.imageURL ?: "",
                    selectedAnswer = ansInt
                )
            }
            
            val mappedOtherTopics = state.customQuestions.filter { it.title.isNotBlank() }.map {
                val ansInt = when (it.answer) {
                    "Yes" -> 1
                    "No" -> 2
                    "Not Applicable" -> 3
                    else -> 0
                }
                org.example.project.data.model.CreatePreTaskOtherTopic(
                    id = it.id,
                    title = it.title,
                    selectedAnswer = ansInt
                )
            }
            
            val mappedAttendees = state.attendees.filter { it.employeeName.isNotBlank() || it.employeeCode.isNotBlank() }.map {
                org.example.project.data.model.CreatePreTaskAttendee(
                    id = "",
                    employeeCode = it.employeeCode,
                    employeeName = it.employeeName,
                    companyName = it.companyName,
                    profession = it.profession
                )
            }
            
            val mappedImages = state.evidences.filter { it.imagePath.isNotBlank() }.map {
                org.example.project.data.model.CreatePreTaskImage(
                    image = it.imagePath,
                    description = it.description,
                    isAiGeneratedDescription = false
                )
            }
            
            val request = org.example.project.data.model.CreatePreTaskRequest(
                date = formattedDate,
                startTime = state.startTime + ":00",
                endTime = state.endTime + ":00",
                msraReference = state.msraReference,
                permitReference = state.permitReference,
                taskTitle = state.taskTitle,
                contents = mappedContents,
                questions = mappedQuestions,
                otherTopic = mappedOtherTopics,
                attendees = mappedAttendees,
                createdAt = currentCreatedAt,
                facilitiesId = state.selectedProject.groupId.toString(),
                images = mappedImages,
                reportedBy = state.reportedBy?.userId?.toString() ?: "",
                notes = state.stepByStepAccount,
                sendNotificationTo = state.selectedNotifyPerson?.userId?.let { listOf(it) } ?: emptyList()
            )
            
            when (val result = preTaskRepository.createPreTask(request)) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(isPublishing = false, publishSuccess = true) }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isPublishing = false, error = result.message ?: "Failed to create PreTask") }
                }
            }
        }
    }
}
