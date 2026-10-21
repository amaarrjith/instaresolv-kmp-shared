package org.example.project.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
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
import org.example.project.data.repository.PreTaskDraftRepository
import org.example.project.data.model.CreatePreTaskDraftRequest
import org.example.project.shared.db.AppDatabase
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.project.domain.repository.ProjectRepository
import org.example.project.network.NetworkResult
import kotlinx.serialization.Serializable
import org.example.project.data.settings.parseTimeToMinutes
import instaresolv.shared.generated.resources.*
import org.jetbrains.compose.resources.getString
import instaresolv.shared.generated.resources.error_enter_question
import instaresolv.shared.generated.resources.error_select_answer

@Serializable
data class AttendeeItem(
    val employeeCode: String = "",
    val employeeName: String = "",
    val companyName: String = "",
    val profession: String = ""
)

@Serializable
data class EvidenceImage(
    val imagePath: String = "",
    val description: String = ""
)

@Serializable
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
    val attendees: List<AttendeeItem> = emptyList(),
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
    val bulkHasMore: Boolean = true,
    val isDraftOutdated: Boolean = false
)

class CreatePreTaskViewModel(
    private val projectRepository: ProjectRepository,
    private val preTaskRepository: PreTaskRepository,
    private val authPreferences: AuthPreferences,
    private val preTaskDraftRepository: PreTaskDraftRepository,
    private val database: AppDatabase
) : ViewModel() {

    var draftId: Long = 0L

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
    }

    private var isInitialized = false

    fun initialize(isFromDraft: Boolean, draftId: Long) {
        if (isInitialized) return
        isInitialized = true
        if (isFromDraft && draftId != -1L) {
            loadDraft(draftId)
        } else {
            fetchTopics()
        }
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
                            error = result.message ?: getString(Res.string.error_failed_to_load_topics)
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

    
    fun onNotifyPersonSelected(user: GroupUser?) {
        _uiState.update { it.copy(selectedNotifyPerson = user) }
    }
    
    fun onTaskTitleChanged(title: String) {
        _uiState.update { it.copy(taskTitle = title) }
    }
    
    fun onDateSelected(millis: Long) {
        _uiState.update { it.copy(dateMillis = millis) }
        _uiState.update { it.copy(startTime = "") }
        _uiState.update { it.copy(endTime = "") }
    }

    fun onStartTimeChanged(time: String) {
        _uiState.update { it.copy(endTime = "") }
        _uiState.update { it.copy(startTime = time) }
    }

    fun onEndTimeChanged(time: String) {
        _uiState.update { state ->
            if (state.startTime.isNotBlank() && parseTimeToMinutes(time) <= parseTimeToMinutes(state.startTime)) {
                state
            } else {
                state.copy(endTime = time)
            }
        }
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
    fun addCustomQuestion(errorMessage: String? = null): Boolean {
        val lastQuestion = _uiState.value.customQuestions.lastOrNull()
        if (lastQuestion != null && (lastQuestion.title.isBlank() || lastQuestion.answer.isBlank())) {
            if (errorMessage != null) {
                _uiState.update { it.copy(error = errorMessage) }
            }
            return false
        }
        _uiState.update {
            val newId = (it.customQuestions.maxOfOrNull { q -> q.id } ?: 0) + 1
            it.copy(customQuestions = it.customQuestions + CustomQuestion(id = newId))
        }
        return true
    }

    fun removeCustomQuestion(id: Int) {
        _uiState.update {
            val remaining = it.customQuestions.filter { q -> q.id != id }
            it.copy(customQuestions = remaining.ifEmpty { listOf(CustomQuestion(1)) })
        }
    }

    fun updateCustomQuestionTitle(id: Int, title: String) {
        _uiState.update {
            val updated = it.customQuestions.map { q -> 
                if (q.id == id) {
                    q.copy(
                        title = title,
                        answer = if (title.isBlank()) "" else q.answer
                    )
                } else q 
            }
            it.copy(customQuestions = updated)
        }
    }

    fun updateCustomQuestionAnswer(id: Int, answer: String): Boolean {
        val question = _uiState.value.customQuestions.find { it.id == id }
        if (question == null || question.title.isBlank()) {
            return false
        }
        _uiState.update {
            val updated = it.customQuestions.map { q -> if (q.id == id) q.copy(answer = answer) else q }
            it.copy(customQuestions = updated)
        }
        return true
    }

    // Attendees
    fun addAttendee(attendee: AttendeeItem = AttendeeItem()) {
        if (attendee.employeeCode.isBlank() && attendee.employeeName.isBlank()) return
        _uiState.update {
            val cleanList = it.attendees.filter { a -> a.employeeCode.isNotBlank() || a.employeeName.isNotBlank() }
            it.copy(attendees = cleanList + attendee)
        }
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
        if (state.isBulkLoading || (!state.bulkHasMore && isLoadMore)) return

        viewModelScope.launch {
            val page = if (isLoadMore) state.bulkPageNumber + 1 else 1
            _uiState.update { it.copy(isBulkLoading = true) }

            val result = projectRepository.getEmployeeAllList(
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
            if (state.taskTitle.isBlank()) {
                _uiState.update { it.copy(isPublishing = false, error = getString(Res.string.error_task_title_required)) }
                return@launch
            }
            if (state.dateMillis == null) {
                _uiState.update { it.copy(isPublishing = false, error = getString(Res.string.error_date_required)) }
                return@launch
            }

            val incompleteCustomQ = state.customQuestions.find { it.title.isNotBlank() && it.answer.isBlank() }
            if (incompleteCustomQ != null) {
                val errorMsg = getString(Res.string.error_select_answer)
                _uiState.update { it.copy(isPublishing = false, error = errorMsg) }
                return@launch
            }
            if (state.startTime.isBlank()) {
                _uiState.update { it.copy(isPublishing = false, error = getString(Res.string.error_start_time_required)) }
                return@launch
            }
            if (state.endTime.isBlank()) {
                _uiState.update { it.copy(isPublishing = false, error = getString(Res.string.error_end_time_required)) }
                return@launch
            }
            if (parseTimeToMinutes(state.endTime) <= parseTimeToMinutes(state.startTime)) {
                val errorMsg = getString(Res.string.error_end_time_greater)
                _uiState.update { it.copy(isPublishing = false, error = errorMsg) }
                return@launch
            }

            val isToday = state.dateMillis?.let { millis ->
                val selectedDate = Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.UTC).date
                val today = kotlin.time.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                selectedDate == today
            } ?: false

            if (isToday) {
                val currentMinutes = run {
                    val now = kotlin.time.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                    now.hour * 60 + now.minute
                }
                if (parseTimeToMinutes(state.startTime) > currentMinutes || parseTimeToMinutes(state.endTime) > currentMinutes) {
                    val errorMsg = getString(Res.string.error_cannot_select_future_time)
                    _uiState.update { it.copy(isPublishing = false, error = errorMsg) }
                    return@launch
                }
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
            
            val mappedAttendees = state.attendees.filter { it.employeeName.isNotBlank() || it.employeeCode.isNotBlank() }.mapIndexed { index, item ->
                org.example.project.data.model.CreatePreTaskAttendee(
                    id = index + 1,
                    employeeCode = item.employeeCode,
                    employeeName = item.employeeName,
                    companyName = item.companyName,
                    profession = item.profession
                )
            }
            
            val mappedImages = state.evidences.filter { it.imagePath.isNotBlank() }.map {
                org.example.project.data.model.CreatePreTaskImage(
                    image = it.imagePath,
                    description = it.description,
                    isAiGeneratedDescription = false
                )
            }

            mappedQuestions.forEach { question ->
                if (question.selectedAnswer == 0) {
                    _uiState.update { it.copy(isPublishing = false, error = getString(Res.string.error_all_questions_must_be_answered)) }
                    return@launch
                }
            }
            
            val request = org.example.project.data.model.CreatePreTaskRequest(
                date = formattedDate,
                startTime = convertTimeTo24HourFormat(state.startTime),
                endTime = convertTimeTo24HourFormat(state.endTime),
                msraReference = state.msraReference,
                permitReference = state.permitReference,
                taskTitle = state.taskTitle,
                contents = mappedContents,
                questions = mappedQuestions,
                otherTopic = mappedOtherTopics,
                attendees = mappedAttendees,
                createdAt = currentCreatedAt,
                facilitiesId = state.selectedProject?.groupId?.toString() ?: "",
                images = mappedImages,
                reportedBy = state.reportedBy?.userId?.toString() ?: "",
                notes = state.stepByStepAccount,
                sendNotificationTo = state.selectedNotifyPerson?.userId?.let { listOf(it) } ?: emptyList()
            )
            
            when (val result = preTaskRepository.createPreTask(request)) {
                is NetworkResult.Success -> {
                    if (draftId != 0L) {
                        preTaskDraftRepository.deleteDraft(draftId)
                    }
                    _uiState.update { it.copy(isPublishing = false, publishSuccess = true) }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isPublishing = false, error = result.message ?: getString(Res.string.error_failed_to_create_pretask)) }
                }
            }
        }
    }

    private fun convertTimeTo24HourFormat(timeStr: String): String {
        try {
            val parts = timeStr.split(" : ", " ")
            if (parts.size >= 3) {
                val hourStr = parts[0]
                val minuteStr = parts[1]
                val amPm = parts[2].uppercase()

                var hour = hourStr.toIntOrNull() ?: 0
                if (amPm == "AM" && hour == 12) hour = 0
                if (amPm == "PM" && hour < 12) hour += 12

                return "${hour.toString().padStart(2, '0')}:${minuteStr}:00"
            }
        } catch (e: Exception) {
            // Fallback
        }
        return timeStr
    }

    fun saveLocalDraft(
        id: Long,
        facilitiesId: Int?,
        projectJson: String?,
        dateMillis: Long?,
        startTime: String,
        endTime: String,
        msraReference: String,
        permitReference: String,
        taskTitle: String,
        stepByStepAccount: String,
        contentsJson: String,
        questionsJson: String,
        questionAnswersJson: String,
        customQuestionsJson: String,
        attendeesJson: String,
        evidencesJson: String,
        selectedNotifyPersonJson: String?,
        onSuccess: () -> Unit
    ) {
        if (facilitiesId == null && dateMillis == null && startTime.isBlank() && endTime.isBlank() &&
            taskTitle.isBlank() && msraReference.isBlank() && permitReference.isBlank() &&
            stepByStepAccount.isBlank()) {
            viewModelScope.launch {
                _uiState.update { it.copy(error = getString(Res.string.error_draft_at_least_one_field)) }
            }
            return
        }

        if (startTime.isNotBlank() && endTime.isNotBlank() && parseTimeToMinutes(endTime) <= parseTimeToMinutes(startTime)) {
            viewModelScope.launch {
                _uiState.update { it.copy(error = getString(Res.string.error_end_time_greater)) }
            }
            return
        }

        val isToday = dateMillis?.let { millis ->
            val selectedDate = Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.UTC).date
            val today = kotlin.time.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            selectedDate == today
        } ?: false

        if (isToday) {
            val currentMinutes = run {
                val now = kotlin.time.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                now.hour * 60 + now.minute
            }
            if ((startTime.isNotBlank() && parseTimeToMinutes(startTime) > currentMinutes) ||
                (endTime.isNotBlank() && parseTimeToMinutes(endTime) > currentMinutes)) {
                viewModelScope.launch {
                    _uiState.update { it.copy(error = getString(Res.string.error_cannot_select_future_time)) }
                }
                return
            }
        }

        viewModelScope.launch {
            val user = authPreferences.getLoggedInUser()
            val request = CreatePreTaskDraftRequest(
                id = id,
                facilitiesId = facilitiesId,
                projectJson = projectJson,
                dateMillis = dateMillis,
                startTime = startTime,
                endTime = endTime,
                msraReference = msraReference,
                permitReference = permitReference,
                taskTitle = taskTitle,
                stepByStepAccount = stepByStepAccount,
                contentsJson = contentsJson,
                questionsJson = questionsJson,
                questionAnswersJson = questionAnswersJson,
                customQuestionsJson = customQuestionsJson,
                attendeesJson = attendeesJson,
                evidencesJson = evidencesJson,
                selectedNotifyPersonJson = selectedNotifyPersonJson,
                reportedBy = user?.name ?: "",
                createdAt = run {
                    val localDateTime = kotlin.time.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                    val year = localDateTime.year
                    val month = localDateTime.monthNumber.toString().padStart(2, '0')
                    val day = localDateTime.dayOfMonth.toString().padStart(2, '0')
                    val hour = localDateTime.hour.toString().padStart(2, '0')
                    val minute = localDateTime.minute.toString().padStart(2, '0')
                    val second = localDateTime.second.toString().padStart(2, '0')
                    "$year-$month-$day $hour:$minute:$second"
                },
                userId = user?.userId ?: -1
            )
            preTaskDraftRepository.saveDraft(request)
            onSuccess()
        }
    }

    private data class PreTaskDraftLoadedData(
        val isOutdated: Boolean,
        val project: Project?,
        val contents: List<PreTaskContentData>,
        val questions: List<PreTaskQuestionData>,
        val questionAnswers: Map<Int, String>,
        val customQuestions: List<CustomQuestion>,
        val attendees: List<AttendeeItem>,
        val evidences: List<EvidenceImage>,
        val notifyPerson: GroupUser?
    )

    fun loadDraft(id: Long) {
        viewModelScope.launch {
            draftId = id
            val draft = preTaskDraftRepository.getDraftById(id) ?: return@launch
            
            val loaded = withContext(Dispatchers.IO) {
                val draftContents = draft.contentsJson?.let { Json.decodeFromString<List<PreTaskContentData>>(it) } ?: emptyList()
                val dbContents = database.appDatabaseQueries.getAllPreTaskContents().executeAsList()
                val draftContentIds = draftContents.map { it.id.toLong() }.toSet()
                val dbContentIds = dbContents.map { it.id }.toSet()
                val dbContentsById = dbContents.associateBy { it.id }
                val isContentOutdated = draftContents.isNotEmpty() && (
                    draftContentIds != dbContentIds || draftContents.any { draftC ->
                        val dbC = dbContentsById[draftC.id.toLong()]
                        dbC == null || dbC.updatedTime != draftC.updatedTime
                    }
                )

                val draftQuestions = draft.questionsJson?.let { Json.decodeFromString<List<PreTaskQuestionData>>(it) } ?: emptyList()
                val dbQuestions = database.appDatabaseQueries.getAllPreTaskQuestions().executeAsList()
                val draftQuestionIds = draftQuestions.map { it.id.toLong() }.toSet()
                val dbQuestionIds = dbQuestions.map { it.id }.toSet()
                val dbQuestionsById = dbQuestions.associateBy { it.id }
                val isQuestionOutdated = draftQuestions.isNotEmpty() && (
                    draftQuestionIds != dbQuestionIds || draftQuestions.any { draftQ ->
                        val dbQ = dbQuestionsById[draftQ.id.toLong()]
                        dbQ == null || dbQ.updatedTime != draftQ.updatedTime
                    }
                )

                PreTaskDraftLoadedData(
                    isOutdated = isContentOutdated || isQuestionOutdated,
                    project = draft.projectJson?.let { Json.decodeFromString<Project>(it) },
                    contents = draft.contentsJson?.let { Json.decodeFromString<List<PreTaskContentData>>(it) } ?: emptyList(),
                    questions = draftQuestions,
                    questionAnswers = draft.questionAnswersJson?.let { Json.decodeFromString<Map<Int, String>>(it) } ?: emptyMap(),
                    customQuestions = draft.customQuestionsJson?.let { Json.decodeFromString<List<CustomQuestion>>(it) } ?: listOf(CustomQuestion(1)),
                    attendees = draft.attendeesJson?.let { 
                        Json.decodeFromString<List<AttendeeItem>>(it).filter { a -> a.employeeCode.isNotBlank() || a.employeeName.isNotBlank() } 
                    } ?: emptyList(),
                    evidences = draft.evidencesJson?.let { Json.decodeFromString<List<EvidenceImage>>(it) } ?: listOf(EvidenceImage()),
                    notifyPerson = draft.selectedNotifyPersonJson?.let { Json.decodeFromString<GroupUser>(it) }
                )
            }
            
            val isDraftToday = draft.dateMillis?.let { millis ->
                val selectedDate = Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.UTC).date
                val today = kotlin.time.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                selectedDate == today
            } ?: false

            val currentMinutesNow = run {
                val now = kotlin.time.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                now.hour * 60 + now.minute
            }

            val sanitizedStartTime = if (isDraftToday && !draft.startTime.isNullOrBlank() && parseTimeToMinutes(draft.startTime) > currentMinutesNow) {
                ""
            } else {
                draft.startTime ?: ""
            }

            val sanitizedEndTime = if (
                sanitizedStartTime.isBlank() ||
                draft.endTime.isNullOrBlank() ||
                parseTimeToMinutes(draft.endTime) <= parseTimeToMinutes(sanitizedStartTime) ||
                (isDraftToday && parseTimeToMinutes(draft.endTime) > currentMinutesNow)
            ) {
                ""
            } else {
                draft.endTime ?: ""
            }

            _uiState.update { state ->
                state.copy(
                    selectedProject = loaded.project,
                    taskTitle = draft.taskTitle ?: "",
                    dateMillis = draft.dateMillis,
                    startTime = sanitizedStartTime,
                    endTime = sanitizedEndTime,
                    msraReference = draft.msraReference ?: "",
                    permitReference = draft.permitReference ?: "",
                    stepByStepAccount = draft.stepByStepAccount ?: "",
                    contents = loaded.contents,
                    questions = loaded.questions,
                    questionAnswers = loaded.questionAnswers,
                    customQuestions = loaded.customQuestions,
                    attendees = loaded.attendees,
                    evidences = loaded.evidences,
                    selectedNotifyPerson = loaded.notifyPerson,
                    isDraftOutdated = loaded.isOutdated
                )
            }
            loaded.project?.let { fetchGroupUsers(it.groupId.toString(), it.groupCode ?: "") }
        }
    }
}
