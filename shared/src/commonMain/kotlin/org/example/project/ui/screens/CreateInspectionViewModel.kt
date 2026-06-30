package org.example.project.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.toLocalDateTime
import org.example.project.data.model.Project
import org.example.project.data.settings.AuthPreferences
import org.example.project.ui.screens.IncidentImage

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
    val questions: List<org.example.project.data.model.StaticEquipmentContent> = emptyList(),
    val answers: Map<Int, String> = emptyMap(),
    val isFetchingQuestions: Boolean = false
)

class CreateInspectionViewModel(
    private val authPreferences: AuthPreferences,
    private val inspectionRepository: org.example.project.domain.repository.InspectionRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateInspectionState())
    val uiState: StateFlow<CreateInspectionState> = _uiState.asStateFlow()
    val user = authPreferences.getLoggedInUser()

    fun init(inspectionTypeId: Int, inspectionTypeName: String) {
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
        if (state.equipmentDescription.isBlank()) {
            _uiState.value = state.copy(error = "Model Number is required")
            return
        }
        if (state.inspectionDateMillis == null) {
            _uiState.value = state.copy(error = "Date is required")
            return
        }
        if (state.equipmentSource.isBlank()) {
            _uiState.value = state.copy(error = "Equipment Source is required")
            return
        }
        if (state.questions.isNotEmpty() && state.answers.size < state.questions.size) {
            _uiState.value = state.copy(error = "Please answer all questions")
            return
        }
        
        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = null)
            
            val equipmentSourceInt = when (state.equipmentSource) {
                "ALNASR" -> 1
                "RENTAL" -> 2
                "SUBCONTRACTOR" -> 3
                else -> 1
            }

            val staticEquipments = state.answers.map { (id, answer) ->
                val selectedValue = when (answer) {
                    "Yes" -> 1
                    "No" -> 2
                    else -> 3 // NA
                }
                org.example.project.data.model.StaticEquipmentAnswer(
                    equipmentId = id,
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
                modelNumber = state.equipmentDescription,
                inspectedBy = user?.name ?: "",
                location = state.location,
                inspectionDate = dateStr,
                description = state.description,
                equipmentSource = equipmentSourceInt,
                subContractor = if (state.equipmentSource == "SUBCONTRACTOR") state.equipmentSourceSecondary else null,
                staticEquipment = staticEquipments,
                notes = state.notes,
                images = images
            )

            val result = inspectionRepository.addInspection(request)
            when (result) {
                is org.example.project.network.NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false)
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
