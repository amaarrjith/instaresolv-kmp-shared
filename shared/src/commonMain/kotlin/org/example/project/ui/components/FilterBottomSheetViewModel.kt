package org.example.project.ui.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.data.model.FilterContentData
import org.example.project.data.remote.api.AuthApiService
import org.example.project.network.NetworkResult

data class FilterBottomSheetUiState(
    val isLoading: Boolean = false,
    val filterData: FilterContentData? = null,
    val error: String? = null
)

class FilterBottomSheetViewModel(
    private val apiService: AuthApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(FilterBottomSheetUiState())
    val uiState: StateFlow<FilterBottomSheetUiState> = _uiState.asStateFlow()

    init {
        fetchFilterContent()
    }

    private fun fetchFilterContent() {
        if (_uiState.value.filterData != null) return // Already fetched
        
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            when (val result = apiService.getFilterContent()) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, filterData = result.data, error = null) }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
            }
        }
    }
}
