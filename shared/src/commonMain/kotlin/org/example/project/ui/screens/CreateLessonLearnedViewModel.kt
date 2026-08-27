package org.example.project.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.data.model.CreateLessonLearnedRequest
import org.example.project.data.model.CreateLessonLearnedResponseData
import org.example.project.data.model.LessonLearnedImageRequest
import org.example.project.data.settings.AuthPreferences
import org.example.project.domain.repository.LessonLearnedRepository
import org.example.project.data.repository.LessonLearnedDraftRepository
import org.example.project.network.NetworkResult
import kotlin.time.Clock
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.TimeZone

sealed class CreateLessonLearnedUiState {
    object Idle : CreateLessonLearnedUiState()
    object Loading : CreateLessonLearnedUiState()
    data class Success(val response: CreateLessonLearnedResponseData) : CreateLessonLearnedUiState()
    data class Error(val message: String) : CreateLessonLearnedUiState()
}

class CreateLessonLearnedViewModel(
    private val repository: LessonLearnedRepository,
    private val authPreferences: AuthPreferences,
    private val lessonLearnedDraftRepository: LessonLearnedDraftRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CreateLessonLearnedUiState>(CreateLessonLearnedUiState.Idle)
    val uiState: StateFlow<CreateLessonLearnedUiState> = _uiState.asStateFlow()
    val user = authPreferences.getLoggedInUser()

    var draftId: Long = 0L

    fun resetState() {
        _uiState.value = CreateLessonLearnedUiState.Idle
    }

    fun createLessonLearned(
        title: String,
        description: String?,
        reportedBy: String,
        images: List<LessonLearnedImageRequest>? = emptyList(),
        facilitiesId: Int
    ) {
        viewModelScope.launch {
            _uiState.value = CreateLessonLearnedUiState.Loading
            
            val request = CreateLessonLearnedRequest(
                facilitiesId = facilitiesId.toString(),
                title = title,
                description = description ?: "",
                reportedBy = reportedBy,
                images = images ?: emptyList()
            )
            
            when (val result = repository.createLessonLearned(request)) {
                is NetworkResult.Success -> {
                    if (draftId != 0L) {
                        lessonLearnedDraftRepository.deleteDraft(draftId)
                    }
                    _uiState.value = CreateLessonLearnedUiState.Success(result.data)
                }
                is NetworkResult.Error -> {
                    _uiState.value = CreateLessonLearnedUiState.Error(result.message)
                }
            }
        }
    }

    fun saveLocalDraft(
        id: Long,
        facilitiesId: Int?,
        projectJson: String?,
        title: String,
        description: String,
        imagesJson: String,
        onSuccess: () -> Unit
    ) {
        if (facilitiesId == null && title.isBlank() && description.isBlank() && (imagesJson.isBlank() || imagesJson == "[]")) {
            _uiState.value = CreateLessonLearnedUiState.Error("At least one field must have a value to save as draft")
            return
        }

        viewModelScope.launch {
            val request = org.example.project.data.model.CreateLessonLearnedDraftRequest(
                id = id,
                facilitiesId = facilitiesId,
                projectJson = projectJson,
                title = title,
                description = description,
                reportedBy = user?.name ?: "",
                imagesJson = imagesJson,
                createdAt = run {
                    val localDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
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
            lessonLearnedDraftRepository.saveDraft(request)
            onSuccess()
        }
    }

    suspend fun getDraftById(id: Long): org.example.project.shared.db.LessonLearnedDraft? {
        return lessonLearnedDraftRepository.getDraftById(id)
    }
}
