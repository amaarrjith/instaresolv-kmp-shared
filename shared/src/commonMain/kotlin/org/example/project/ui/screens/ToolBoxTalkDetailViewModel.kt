package org.example.project.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.data.model.ToolBoxTalkDetailRequest
import org.example.project.data.model.ToolBoxTalkItem
import org.example.project.domain.repository.ToolBoxTalkRepository
import org.example.project.network.NetworkResult

sealed class ToolBoxTalkDetailUiState {
    object Loading : ToolBoxTalkDetailUiState()
    data class Success(val data: ToolBoxTalkItem) : ToolBoxTalkDetailUiState()
    data class Error(val message: String) : ToolBoxTalkDetailUiState()
}

class ToolBoxTalkDetailViewModel(
    private val repository: ToolBoxTalkRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ToolBoxTalkDetailUiState>(ToolBoxTalkDetailUiState.Loading)
    val uiState: StateFlow<ToolBoxTalkDetailUiState> = _uiState.asStateFlow()

    fun loadToolBoxTalkDetail(id: Int) {
        viewModelScope.launch {
            _uiState.value = ToolBoxTalkDetailUiState.Loading
            when (val result = repository.getToolBoxTalkDetail(ToolBoxTalkDetailRequest(id))) {
                is NetworkResult.Success -> {
                    _uiState.value = ToolBoxTalkDetailUiState.Success(result.data)
                }
                is NetworkResult.Error -> {
                    _uiState.value = ToolBoxTalkDetailUiState.Error(result.message ?: "Failed to load details")
                }
            }
        }
    }
}
