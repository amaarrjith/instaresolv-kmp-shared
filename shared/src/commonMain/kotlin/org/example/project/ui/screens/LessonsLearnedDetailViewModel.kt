package org.example.project.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.data.model.LessonLearnedDetailRequest
import org.example.project.data.model.LessonLearnedDetailResponseData
import org.example.project.domain.repository.LessonLearnedRepository
import org.example.project.network.NetworkResult

sealed class LessonsLearnedDetailUiState {
    object Loading : LessonsLearnedDetailUiState()
    data class Success(val data: LessonLearnedDetailResponseData) : LessonsLearnedDetailUiState()
    data class Error(val message: String) : LessonsLearnedDetailUiState()
}

class LessonsLearnedDetailViewModel(
    private val repository: LessonLearnedRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LessonsLearnedDetailUiState>(LessonsLearnedDetailUiState.Loading)
    val uiState: StateFlow<LessonsLearnedDetailUiState> = _uiState.asStateFlow()

    fun loadLessonLearnedDetail(id: Int) {
        viewModelScope.launch {
            _uiState.value = LessonsLearnedDetailUiState.Loading
            when (val result = repository.getLessonLearnedDetail(LessonLearnedDetailRequest(id))) {
                is NetworkResult.Success -> {
                    _uiState.value = LessonsLearnedDetailUiState.Success(result.data)
                }
                is NetworkResult.Error -> {
                    _uiState.value = LessonsLearnedDetailUiState.Error(result.message)
                }
            }
        }
    }
}
