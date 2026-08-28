package org.example.project.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import org.example.project.data.model.CreateInspectionDraftRequest
import org.example.project.data.model.Project
import org.example.project.data.model.StaticEquipmentContent
import org.example.project.data.repository.InspectionDraftRepository
import org.example.project.data.settings.AuthPreferences
import org.example.project.shared.db.AppDatabase

data class CreateInspectionState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedProject: Project? = null,
    val inspectionTypeId: Int = -1,
    val inspectionTypeName: String = "",
    val equipmentDescription: String = "",
    val location: String = "",
    val inspectionDateMillis: Long? = null,
    val equipmentSource: String = "",
    val description: String = "",
    val equipmentSourceSecondary: String = "",
    val notes: String = "",
    val inspectionImages: List<IncidentImage> = listOf(IncidentImage()),
    val questions: List<StaticEquipmentContent> = emptyList(),
    val answers: Map<Int, String> = emptyMap(),
    val isFetchingQuestions: Boolean = false,
    val isDraftOutdated: Boolean = false,
    val isDraftSaveSuccess: Boolean = false
)

class CreateInspectionViewModel(
    private val authPreferences: AuthPreferences,
    private val inspectionRepository: org.example.project.domain.repository.InspectionRepository,
    private val inspectionDraftRepository: InspectionDraftRepository,
    private val database: AppDatabase
) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateInspectionState())
    val uiState: StateFlow<CreateInspectionState> = _uiState.asStateFlow()
    val user = authPreferences.getLoggedInUser()

    var draftId: Long = 0L
    private var isInitialized = false

    fun initialize(inspectionTypeId: Int, inspectionTypeName: String, isFromDraft: Boolean, draftId: Long) {
        if (isInitialized) return
        isInitialized = true
        if (isFromDraft && draftId != -1L) {
            loadDraft(draftId)
        } else {
            initNew(inspectionTypeId, inspectionTypeName)
        }
    }

    private fun initNew(inspectionTypeId: Int, inspectionTypeName: String) {
        _uiState.value = _uiState.value.copy(
            inspectionTypeId = inspectionTypeId,
            inspectionTypeName = inspectionTypeName
        )
        fetchQuestions(inspectionTypeId)
    }

    private fun fetchQuestions(id: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isFetchingQuestions = true, error = null)
            val request = org.example.project.data.model.StaticEquipmentListRequest(id = id)
            val result = inspectionRepository.getStaticEquipmentsList(request)
            
            when (result) {
                is org.example.project.network.NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isFetchingQuestions = false,
                        questions = result.data?.contents ?: emptyList()
                    )
                }
                is org.example.project.network.NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isFetchingQuestions = false,
                        error = result.message ?: "Failed to fetch questions"
                    )
                }
            }
        }
    }

    fun loadDraft(id: Long) {
        viewModelScope.launch {
            draftId = id
            val draft = inspectionDraftRepository.getDraftById(id) ?: return@launch

            val draftQuestions = draft.questionsJson?.let {
                try { Json.decodeFromString<List<StaticEquipmentContent>>(it) } catch (_: Exception) { emptyList() }
            } ?: emptyList()

            val draftAnswers = draft.answersJson?.let {
                try { Json.decodeFromString<Map<Int, String>>(it) } catch (_: Exception) { emptyMap() }
            } ?: emptyMap()

            val draftImages = draft.imagesJson?.let {
                try { Json.decodeFromString<List<IncidentImage>>(it) } catch (_: Exception) { listOf(IncidentImage()) }
            } ?: listOf(IncidentImage())

            // Outdated check 1: Audit item type updated time
            val typeId = draft.inspectionTypeId
            val dbAuditItems = database.appDatabaseQueries.getAllAuditItems().executeAsList()
            val dbAuditItem = dbAuditItems.find { it.auditItemId == typeId }
            val isTypeOutdated = if (dbAuditItem != null) {
                dbAuditItem.formUpdatedTime != draft.inspectionTypeUpdatedTime
            } else {
                // AuditItem missing from DB — could have been removed
                draft.inspectionTypeUpdatedTime != null
            }

            // Outdated check 2: Inspection content version
            val dbContentCache = database.appDatabaseQueries.getInspectionContentCacheByType(typeId).executeAsOneOrNull()
            val isVersionOutdated = if (dbContentCache != null) {
                dbContentCache.version != draft.inspectionContentVersion
            } else {
                draft.inspectionContentVersion != null
            }

            val isOutdated = isTypeOutdated || isVersionOutdated

            _uiState.value = _uiState.value.copy(
                selectedProject = draft.projectJson?.let {
                    try { Json.decodeFromString<Project>(it) } catch (_: Exception) { null }
                },
                inspectionTypeId = draft.inspectionTypeId.toInt(),
                inspectionTypeName = draft.inspectionTypeName ?: "",
                location = draft.location ?: "",
                inspectionDateMillis = draft.inspectionDateMillis,
                description = draft.description ?: "",
                notes = draft.notes ?: "",
                questions = draftQuestions,
                answers = draftAnswers,
                inspectionImages = draftImages,
                isDraftOutdated = isOutdated
            )
        }
    }

    fun saveLocalDraft(
        id: Long,
        facilitiesId: Int?,
        projectJson: String?,
        location: String,
        inspectionDateMillis: Long?,
        description: String,
        notes: String,
        questionsJson: String,
        answersJson: String,
        imagesJson: String,
        onSuccess: () -> Unit
    ) {
        val state = _uiState.value
        if (facilitiesId == null && inspectionDateMillis == null && location.isBlank() && description.isBlank() && notes.isBlank()) {
            _uiState.value = state.copy(error = "At least one value is needed to save as draft")
            return
        }

        viewModelScope.launch {
            val currentUser = authPreferences.getLoggedInUser()
            val typeId = state.inspectionTypeId

            // Snapshot current audit item updated time from DB
            val dbAuditItems = database.appDatabaseQueries.getAllAuditItems().executeAsList()
            val dbAuditItem = dbAuditItems.find { it.auditItemId == typeId.toLong() }
            val auditItemUpdatedTime = dbAuditItem?.formUpdatedTime

            // Snapshot current content version from DB
            val dbContentCache = database.appDatabaseQueries.getInspectionContentCacheByType(typeId.toLong()).executeAsOneOrNull()
            val contentVersion = dbContentCache?.version

            val request = CreateInspectionDraftRequest(
                id = id,
                facilitiesId = facilitiesId,
                projectJson = projectJson,
                inspectionTypeId = typeId,
                inspectionTypeName = state.inspectionTypeName,
                inspectionTypeUpdatedTime = auditItemUpdatedTime,
                inspectionContentVersion = contentVersion,
                location = location,
                inspectionDateMillis = inspectionDateMillis,
                description = description,
                notes = notes,
                questionsJson = questionsJson,
                answersJson = answersJson,
                imagesJson = imagesJson,
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
                userId = currentUser?.userId ?: -1
            )
            inspectionDraftRepository.saveDraft(request)
            onSuccess()
        }
    }

    fun onAnswerChanged(questionId: Int, answer: String) {
        val currentAnswers = _uiState.value.answers.toMutableMap()
        currentAnswers[questionId] = answer
        _uiState.value = _uiState.value.copy(answers = currentAnswers)
    }

    fun onProjectSelected(project: Project?) {
        _uiState.value = _uiState.value.copy(selectedProject = project)
    }
    
    fun onLocationChanged(loc: String) {
        _uiState.value = _uiState.value.copy(location = loc)
    }
    
    fun onDateSelected(millis: Long?) {
        _uiState.value = _uiState.value.copy(inspectionDateMillis = millis)
    }
    
    fun onEquipmentSourceChanged(source: String) {
        _uiState.value = _uiState.value.copy(equipmentSource = source)
    }
    
    fun onEquipmentDescriptionChanged(desc: String) {
        _uiState.value = _uiState.value.copy(equipmentDescription = desc)
    }

    fun onEquipmentSourceSecondaryChanged(name: String) {
        _uiState.value = _uiState.value.copy(equipmentSourceSecondary = name)
    }

    fun onDescriptionChanged(desc: String) {
        _uiState.value = _uiState.value.copy(description = desc)
    }

    fun onNotesChanged(notes: String) {
        _uiState.value = _uiState.value.copy(notes = notes)
    }

    fun onImageDescriptionChange(index: Int, description: String) {
        val currentImages = _uiState.value.inspectionImages.toMutableList()
        if (index in currentImages.indices) {
            currentImages[index] = currentImages[index].copy(description = description)
            _uiState.value = _uiState.value.copy(inspectionImages = currentImages)
        }
    }

    fun onImageSelected(index: Int, url: String) {
        val currentImages = _uiState.value.inspectionImages.toMutableList()
        if (index in currentImages.indices) {
            currentImages[index] = currentImages[index].copy(imageUrl = url)
            _uiState.value = _uiState.value.copy(inspectionImages = currentImages)
        }
    }

    fun onImageRemoved(index: Int) {
        val currentImages = _uiState.value.inspectionImages.toMutableList()
        if (index in currentImages.indices) {
            if (currentImages.size > 1) {
                currentImages.removeAt(index)
            } else {
                currentImages[index] = IncidentImage()
            }
            _uiState.value = _uiState.value.copy(inspectionImages = currentImages)
        }
    }

    fun onAddImageSlot() {
        val currentImages = _uiState.value.inspectionImages.toMutableList()
        if (currentImages.size < 6) {
            currentImages.add(IncidentImage())
            _uiState.value = _uiState.value.copy(inspectionImages = currentImages)
        }
    }

    fun saveInspection(isDraft: Boolean, onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.inspectionDateMillis == null) {
            _uiState.value = state.copy(error = "Date is required")
            return
        }
        if (state.questions.isNotEmpty() && state.answers.size < state.questions.size) {
            _uiState.value = state.copy(error = "Please answer all questions")
            return
        }
        
        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = null)
            val staticEquipments = state.answers.map { (id, answer) ->
                val selectedValue = when (answer) {
                    "Yes" -> 1
                    "No" -> 2
                    else -> 3 // NA
                }
                val questionTitle = state.questions.find { it.id == id }?.title ?: ""
                org.example.project.data.model.InspectionStaticEquipment(
                    id = id,
                    title = questionTitle,
                    selectedValue = selectedValue
                )
            }

            val images = state.inspectionImages.filter { it.imageUrl?.isNotBlank() == true }.map {
                org.example.project.data.model.InspectionImageRequest(
                    image = it.imageUrl ?: "",
                    description = it.description,
                    isAiGeneratedDescription = false
                )
            }

            val dateStr = formatMillis(state.inspectionDateMillis)

            val request = org.example.project.data.model.AddInspectionRequest(
                auditItemId = state.inspectionTypeId,
                facilities = state.selectedProject?.groupId,
                inspectedBy = user?.name ?: "",
                location = state.location,
                inspectionDate = dateStr,
                description = state.description,
                subContractor =  "",
                staticEquipment = staticEquipments,
                notes = state.notes,
                images = images
            )

            val result = inspectionRepository.addInspection(request)
            when (result) {
                is org.example.project.network.NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    // Delete draft after successful publish if editing a draft
                    if (draftId != 0L) {
                        inspectionDraftRepository.deleteDraft(draftId)
                    }
                    onSuccess()
                }
                is org.example.project.network.NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message ?: "Failed to save inspection"
                    )
                }
            }
        }
    }

    private fun formatMillis(millis: Long?): String {
        if (millis == null) return ""
        val instant = kotlinx.datetime.Instant.fromEpochMilliseconds(millis)
        val localDate = instant.toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).date
        val day = localDate.dayOfMonth.toString().padStart(2, '0')
        val month = localDate.monthNumber.toString().padStart(2, '0')
        val year = localDate.year
        return "$day-$month-$year"
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

}
