package org.example.project.homescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.data.UserRoleCheckRequest
import org.example.project.data.model.ApproveRejectRequest
import org.example.project.data.model.PreTaskContentRequest
import org.example.project.data.model.PreTaskContentData
import org.example.project.data.model.PreTaskQuestionData
import org.example.project.data.settings.AuthPreferences
import org.example.project.domain.repository.AuthRepository
import org.example.project.domain.repository.InspectionRepository
import org.example.project.domain.repository.ObservationRepository
import org.example.project.domain.repository.PermitRepository
import org.example.project.domain.repository.PreTaskRepository
import org.example.project.domain.repository.ProjectRepository
import org.example.project.network.NetworkResult
import org.example.project.shared.db.AppDatabase
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import org.example.project.data.model.InspectionContentsRequest
import org.example.project.data.model.InspectionContentsResponse
import org.example.project.data.model.AuditItemsResponse

class HomeScreenViewModel(
    private val authRepository: AuthRepository,
    private val preferences: AuthPreferences,
    private val permitRepository: PermitRepository,
    private val observationRepository: ObservationRepository,
    private val projectRepository: ProjectRepository,
    private val preTaskRepository: PreTaskRepository,
    private val inspectionRepository: InspectionRepository,
    private val database: AppDatabase
): ViewModel()  {
    val user = preferences.getLoggedInUser()
    val userInfo = preferences.getLoggedInUserInfo()

    init {
        fetchDesignationTypes()
        getPreTaskContentsList()
        getAuditsInspectionForms()
        getInspectionContentsList()
    }

    private fun fetchDesignationTypes() {
        viewModelScope.launch {
            when (val result = authRepository.getDesignationTypes()) {
                is NetworkResult.Success -> {
                    preferences.saveDesignationTypes(result.data)
                }
                is NetworkResult.Error -> {
                    // Fail silently for background config fetch
                }
            }
        }
    }

    private val _isGeneratingPdf = MutableStateFlow(false)
    val isGeneratingPdf: StateFlow<Boolean> = _isGeneratingPdf

    private val _pdfUrl = MutableStateFlow<String?>(null)
    val pdfUrl: StateFlow<String?> = _pdfUrl

    private val _pdfToastMessage = MutableStateFlow<String?>(null)
    val pdfToastMessage: StateFlow<String?> = _pdfToastMessage
    private val _pdfErrorToastMessage = MutableStateFlow<String?>(null)
    val pdfErrorToastMessage: StateFlow<String?> = _pdfErrorToastMessage

    private val _isActionLoading = MutableStateFlow(false)
    val isActionLoading: StateFlow<Boolean> = _isActionLoading

    private val _pdfModuleType = MutableStateFlow("Observation")
    val pdfModuleType: StateFlow<String> = _pdfModuleType

    private val _groupUsers = MutableStateFlow<List<org.example.project.data.model.GroupUser>>(emptyList())
    val groupUsers: StateFlow<List<org.example.project.data.model.GroupUser>> = _groupUsers

    fun generatePermitPDF(permitId: Int) {
        viewModelScope.launch {
            _pdfModuleType.value = "Permit"
            _isGeneratingPdf.value = true
            val request = org.example.project.data.model.GeneratePermitPdfRequest(permitId = permitId)
            when (val result = permitRepository.generatePermitPdf(request)) {
                is NetworkResult.Success -> {
                    _isGeneratingPdf.value = false
                    val url = result.data.pdfUrl ?: result.data.excelUrl
                    if (!url.isNullOrBlank()) {
                        _pdfUrl.value = url
                    } else {
                        _pdfToastMessage.value = result.data.statusMessage ?: "Failed to generate Permit PDF"
                    }
                }
                is NetworkResult.Error -> {
                    _isGeneratingPdf.value = false
                    _pdfErrorToastMessage.value = result.message ?: "Failed to generate Permit PDF"
                }
            }
        }
    }

    fun generatePdf(observationId: Int) {
        viewModelScope.launch {
            _pdfModuleType.value = "Observation"
            _isGeneratingPdf.value = true
            val request = org.example.project.data.model.GenerateObservationPdfRequest(observationId = observationId)
            when (val result = observationRepository.generatePdf(request)) {
                is NetworkResult.Success -> {
                    _isGeneratingPdf.value = false
                    val url = result.data.pdfUrl ?: result.data.excelUrl
                    if (!url.isNullOrBlank()) {
                        _pdfUrl.value = url
                    } else {
                        _pdfToastMessage.value = result.data.statusMessage ?: "Failed to generate PDF"
                    }
                }
                is NetworkResult.Error -> {
                    _isGeneratingPdf.value = false
                    _pdfErrorToastMessage.value = result.message ?: "Failed to generate PDF"
                }
            }
        }
    }

    fun setPdfToastMessage(message: String) {
        _pdfToastMessage.value = message
    }

    fun setPdfErrorToastMessage(message: String) {
        _pdfErrorToastMessage.value = message
    }

    fun clearToasts() {
        _pdfToastMessage.value = null
        _pdfErrorToastMessage.value = null
    }

    fun clearPdfUrl() {
        _pdfUrl.value = null
    }

    fun approveOrRejectPendingAction(pendingActionId: Int, action: Int, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _isActionLoading.value = true
            val request = ApproveRejectRequest(pendingActionId, action)
            val response = observationRepository.approveOrReject(request)
            when (response) {
                is NetworkResult.Success -> {
                    _isActionLoading.value = false
                    onSuccess(response.data.statusMessage)
                }
                is NetworkResult.Error -> {
                    _isActionLoading.value = false
                    onError(response.message ?: "Failed to approve or reject")
                }
            }
        }
    }

    fun requestToDeleteObservation(observationId: Int, justification: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _isActionLoading.value = true
            val request = org.example.project.data.model.RequestDeleteObservationRequest(observationId, justification)
            when (val response = observationRepository.requestToDeleteObservation(request)) {
                is NetworkResult.Success -> {
                    _isActionLoading.value = false
                    if (response.data.isSuccess == true) {
                        onSuccess()
                    } else {
                        onError(response.data.statusMessage ?: "Failed to submit request")
                    }
                }
                is NetworkResult.Error -> {
                    _isActionLoading.value = false
                    onError(response.message ?: "Failed to submit request")
                }
            }
        }
    }

    fun requestResponsiblePersonChange(observationId: Int, justification: String, responsiblePerson: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _isActionLoading.value = true
            val request = org.example.project.data.model.RequestResponsiblePersonChangeRequest(observationId, justification, responsiblePerson)
            when (val response = observationRepository.requestResponsiblePersonChange(request)) {
                is NetworkResult.Success -> {
                    _isActionLoading.value = false
                    if (response.data.isSuccess == true) {
                        onSuccess()
                    } else {
                        onError(response.data.statusMessage ?: "Failed to submit request")
                    }
                }
                is NetworkResult.Error -> {
                    _isActionLoading.value = false
                    onError(response.message ?: "Failed to submit request")
                }
            }
        }
    }

    fun fetchGroupUsers(groupId: Int, groupCode: String) {
        println("Called fetchGroupUsers")
        viewModelScope.launch {
            when (val result = projectRepository.getGroupUsers(groupId, groupCode)) {
                is NetworkResult.Success -> {
                    _groupUsers.value = result.data.users
                }
                is NetworkResult.Error -> {
                    _groupUsers.value = emptyList()
                }
            }
        }
    }

    fun userCheckRole(completion: (Boolean)-> Unit) {
        viewModelScope.launch {
            if (user == null) {
                return@launch
            } else {
                val request = UserRoleCheckRequest(user.userId ?: -1)
                when (val result = authRepository.checkUserRole(request)) {
                    is NetworkResult.Success -> {
                        if (user.userRole != result.data.role) {
                            completion(true)
                        } else {
                            completion(false)
                        }
                    }
                    is NetworkResult.Error -> {
                        completion(true)
                    }
                }
            }
        }
    }

    fun getPreTaskContentsList() {
        val contentUpdatedTime = getLatestContentsUpdatedTime() ?: ""
        val questionUpdatedTime = getLatestQuestionsUpdatedTime() ?: ""
        viewModelScope.launch {
            val request = PreTaskContentRequest(
                contentUpdatedTime = contentUpdatedTime,
                questionUpdatedTime = questionUpdatedTime
            )
            when (val result = preTaskRepository.getPreTaskContent(request)) {
                is NetworkResult.Success -> {
                    val response = result.data
                    savePreTaskContents(
                        contents = response.contents ?: emptyList(),
                        deletedContentIds = response.deletedContentsId ?: emptyList(),
                        isContentsEmpty = response.isContentEmpty
                    )
                    savePreTaskQuestions(
                        questions = response.questions ?: emptyList(),
                        deletedQuestionsIds = response.deletedQuestionsId ?: emptyList(),
                        isQuestionsEmpty = response.isQuestionEmpty
                    )
                }
                is NetworkResult.Error -> {
                    println("Error fetching pre-task contents: ${result.message}")
                }
            }
        }
    }

    private fun savePreTaskContents(
        contents: List<PreTaskContentData>,
        deletedContentIds: List<Int>,
        isContentsEmpty: Boolean
    ) {
        val queries = database.appDatabaseQueries
        queries.transaction {
            val existingEntities = queries.getAllPreTaskContents().executeAsList()

            if (isContentsEmpty) {
                queries.clearAllPreTaskContents()
                println("🗑️ Cleared all PreTaskContent records")
                return@transaction
            }

            val existingById = existingEntities.associateBy { it.id }
            val apiIds = contents.map { it.id.toLong() }.toSet()
            val deletedIdsFromAPI = deletedContentIds.map { it.toLong() }.toSet()

            for (content in contents) {
                val contentId = content.id.toLong()
                queries.insertPreTaskContent(
                    id = contentId,
                    title = content.title ?: "",
                    updatedTime = content.updatedTime ?: "",
                    sortOrder = content.order?.toLong() ?: -1L
                )
            }

            for (id in deletedIdsFromAPI) {
                if (existingById.containsKey(id)) {
                    queries.deletePreTaskContent(id)
                    println("🗑️ Deleted local content ID (explicit API deletion): $id")
                }
            }

            val missingIds = existingById.keys - apiIds - deletedIdsFromAPI
            for (id in missingIds) {
                queries.deletePreTaskContent(id)
                println("🗑️ Deleted local content ID (missing in API): $id")
            }
        }
        println("✅ Pre-task contents synced (added/updated/deleted/missing handled).")
    }

    private fun savePreTaskQuestions(
        questions: List<PreTaskQuestionData>,
        deletedQuestionsIds: List<Int>,
        isQuestionsEmpty: Boolean
    ) {
        val queries = database.appDatabaseQueries
        queries.transaction {
            val existingEntities = queries.getAllPreTaskQuestions().executeAsList()

            if (isQuestionsEmpty) {
                queries.clearAllPreTaskQuestions()
                println("🗑️ Cleared all PreTaskQuestion records")
                return@transaction
            }

            val existingById = existingEntities.associateBy { it.id }
            val apiIds = questions.map { it.id.toLong() }.toSet()
            val deletedIdsFromAPI = deletedQuestionsIds.map { it.toLong() }.toSet()

            for (question in questions) {
                val questionId = question.id.toLong()
                queries.insertPreTaskQuestion(
                    id = questionId,
                    contentId = question.contentId?.toLong() ?: 0L,
                    title = question.title ?: "",
                    imageUrl = question.imageURL ?: "",
                    updatedTime = question.updatedTime ?: ""
                )
            }

            for (id in deletedIdsFromAPI) {
                if (existingById.containsKey(id)) {
                    queries.deletePreTaskQuestion(id)
                    println("🗑️ Deleted local question ID (explicit API deletion): $id")
                }
            }

            val missingIds = existingById.keys - apiIds - deletedIdsFromAPI
            for (id in missingIds) {
                queries.deletePreTaskQuestion(id)
                println("🗑️ Deleted local question ID (missing in API): $id")
            }
        }
        println("✅ Pre-task questions synced (added/updated/deleted/missing handled).")
    }

    private fun getLatestContentsUpdatedTime(): String? {
        return database.appDatabaseQueries.getLatestContentUpdatedTime().executeAsOneOrNull()
    }

    private fun getLatestQuestionsUpdatedTime(): String? {
        return database.appDatabaseQueries.getLatestQuestionUpdatedTime().executeAsOneOrNull()
    }

    private fun getLatestAuditItemUpdatedTime(): String? {
        return database.appDatabaseQueries.getLatestAuditItemUpdatedTime().executeAsOneOrNull()?.formUpdatedTime
    }

    private fun getLatestInspectionContentUpdatedTime(): String? {
        return database.appDatabaseQueries.getLatestInspectionContentUpdatedTime().executeAsOneOrNull()?.updatedTime
    }

    fun getAuditsInspectionForms() {
        val updatedTime = getLatestAuditItemUpdatedTime() ?: ""
        viewModelScope.launch {
            when (val result = inspectionRepository.getAuditItems(updatedTime.takeIf { it.isNotBlank() })) {
                is NetworkResult.Success -> {
                    val response = result.data
                    if (response.updatedTime != updatedTime) {
                        val queries = database.appDatabaseQueries
                        queries.transaction {
                            queries.clearAllAuditItems()
                            response.contents.forEach { content ->
                                queries.insertAuditItem(
                                    auditItemId = content.auditItemId.toLong(),
                                    auditItemTitle = content.auditItemTitle,
                                    image = content.image,
                                    formUpdatedTime = response.updatedTime
                                )
                            }
                        }
                        println("✅ Audit items updated locally with latest data!")
                    } else {
                        println("Audit items are already up-to-date — no changes needed.")
                    }
                }
                is NetworkResult.Error -> {
                    println("Failed to fetch audit items: ${result.message}")
                }
            }
        }
    }

    fun getInspectionContentsList() {
        val updatedTime = getLatestInspectionContentUpdatedTime() ?: ""
        viewModelScope.launch {
            val request = InspectionContentsRequest(updatedTime = updatedTime)
            when (val result = inspectionRepository.getInspectionContentsList(request)) {
                is NetworkResult.Success -> {
                    val response = result.data
                    if (response.contentsList.isNotEmpty()) {
                        val queries = database.appDatabaseQueries
                        queries.transaction {
                            response.contentsList.forEach { content ->
                                val type = content.type ?: 0
                                val version = content.version ?: ""
                                val questionsJson = Json.encodeToString(content.contents)
                                
                                val currentInstant = kotlin.time.Clock.System.now()
                                val currentDt = currentInstant.toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
                                val timeNow = "${currentDt.year}-${currentDt.month.number.toString().padStart(2, '0')}-${currentDt.day.toString().padStart(2, '0')} ${currentDt.hour.toString().padStart(2, '0')}:${currentDt.minute.toString().padStart(2, '0')}:${currentDt.second.toString().padStart(2, '0')}"
                                
                                queries.insertInspectionContentCache(
                                    type = type.toLong(),
                                    version = version,
                                    questionsJson = questionsJson,
                                    updatedTime = timeNow
                                )
                            }
                        }
                        println("✅ Inspection contents updated locally with latest data!")
                    } else {
                        println("No new inspection contents to update — already up-to-date.")
                    }
                }
                is NetworkResult.Error -> {
                    println("Failed to fetch inspection contents: ${result.message}")
                }
            }
        }
    }
}