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
import org.example.project.domain.repository.LessonLearnedRepository
import org.example.project.network.NetworkResult

sealed class CreateLessonLearnedUiState {
    object Idle : CreateLessonLearnedUiState()
    object Loading : CreateLessonLearnedUiState()
    data class Success(val response: CreateLessonLearnedResponseData) : CreateLessonLearnedUiState()
    data class Error(val message: String) : CreateLessonLearnedUiState()
}

class CreateLessonLearnedViewModel(
    private val repository: LessonLearnedRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CreateLessonLearnedUiState>(CreateLessonLearnedUiState.Idle)
    val uiState: StateFlow<CreateLessonLearnedUiState> = _uiState.asStateFlow()

    fun resetState() {
        _uiState.value = CreateLessonLearnedUiState.Idle
    }

    fun createLessonLearned(
        title: String,
        description: String?,
        reportedBy: String,
        images: List<LessonLearnedImageRequest>? = null,
        facilitiesId: String? = null
    ) {
        viewModelScope.launch {
            _uiState.value = CreateLessonLearnedUiState.Loading
            
            val request = CreateLessonLearnedRequest(
                faciltiesId = facilitiesId,
                title = title,
                description = description,
                reportedBy = reportedBy,
                images = images
            )
            
            when (val result = repository.createLessonLearned(request)) {
                is NetworkResult.Success -> {
                    _uiState.value = CreateLessonLearnedUiState.Success(result.data)
                }
                is NetworkResult.Error -> {
                    _uiState.value = CreateLessonLearnedUiState.Error(result.message)
                }
            }
        }
    }
}
