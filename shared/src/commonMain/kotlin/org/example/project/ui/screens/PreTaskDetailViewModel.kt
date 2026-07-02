package org.example.project.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.data.model.PreTaskDetailRequest
import org.example.project.data.model.PreTaskDetailResponseData
import org.example.project.domain.repository.PreTaskRepository
import org.example.project.network.NetworkResult

sealed class PreTaskDetailUiState {
    object Loading : PreTaskDetailUiState()
    data class Success(val data: PreTaskDetailResponseData) : PreTaskDetailUiState()
    data class Error(val message: String) : PreTaskDetailUiState()
}

class PreTaskDetailViewModel(
    private val repository: PreTaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PreTaskDetailUiState>(PreTaskDetailUiState.Loading)
    val uiState: StateFlow<PreTaskDetailUiState> = _uiState.asStateFlow()

    fun loadPreTaskDetail(id: Int) {
        viewModelScope.launch {
            _uiState.value = PreTaskDetailUiState.Loading
            when (val result = repository.getPreTaskDetail(PreTaskDetailRequest(id))) {
                is NetworkResult.Success -> {
                    _uiState.value = PreTaskDetailUiState.Success(result.data)
                }
                is NetworkResult.Error -> {
                    _uiState.value = PreTaskDetailUiState.Error(result.message)
                }
            }
        }
    }
}
