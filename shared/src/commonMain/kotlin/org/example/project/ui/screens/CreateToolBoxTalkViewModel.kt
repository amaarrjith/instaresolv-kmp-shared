package org.example.project.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.data.model.CreateToolBoxTalkRequest
import org.example.project.data.model.DiscussionPointRequest
import org.example.project.data.model.AttendeeRequest
import org.example.project.data.model.ToolBoxTalkImageRequest
import org.example.project.data.model.EmployeeData
import org.example.project.data.settings.AuthPreferences
import org.example.project.domain.repository.ToolBoxTalkRepository
import org.example.project.domain.repository.ProjectRepository
import org.example.project.network.NetworkResult
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.project.data.model.ToolBoxAttendeeRequest

sealed class CreateToolBoxTalkUiState {
    object Idle : CreateToolBoxTalkUiState()
    object Loading : CreateToolBoxTalkUiState()
    object Success : CreateToolBoxTalkUiState()
    data class Error(val message: String) : CreateToolBoxTalkUiState()
}

class CreateToolBoxTalkViewModel(
    private val repository: ToolBoxTalkRepository,
    private val projectRepository: ProjectRepository,
    private val authPreferences: AuthPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow<CreateToolBoxTalkUiState>(CreateToolBoxTalkUiState.Idle)
    val uiState: StateFlow<CreateToolBoxTalkUiState> = _uiState.asStateFlow()

    val logginedUser = authPreferences.getLoggedInUser()

    // Bulk Employee Upload States
    private val _isBulkUploadSheetVisible = MutableStateFlow(false)
    val isBulkUploadSheetVisible: StateFlow<Boolean> = _isBulkUploadSheetVisible.asStateFlow()

    private val _bulkEmployees = MutableStateFlow<List<EmployeeData>>(emptyList())
    val bulkEmployees: StateFlow<List<EmployeeData>> = _bulkEmployees.asStateFlow()

    private val _selectedBulkEmployees = MutableStateFlow<Set<Int>>(emptySet())
    val selectedBulkEmployees: StateFlow<Set<Int>> = _selectedBulkEmployees.asStateFlow()

    private val _bulkSearchQuery = MutableStateFlow("")
    val bulkSearchQuery: StateFlow<String> = _bulkSearchQuery.asStateFlow()

    private val _isBulkLoading = MutableStateFlow(false)
    val isBulkLoading: StateFlow<Boolean> = _isBulkLoading.asStateFlow()

    private val _bulkHasMore = MutableStateFlow(true)
    val bulkHasMore: StateFlow<Boolean> = _bulkHasMore.asStateFlow()

    private var bulkPageNumber = 1

    fun resetState() {
        _uiState.value = CreateToolBoxTalkUiState.Idle
    }

    // Bulk Actions
    fun openBulkUploadSheet(facilitiesId: String?) {
        if (facilitiesId.isNullOrBlank()) return
        _isBulkUploadSheetVisible.value = true
        fetchBulkEmployees(facilitiesId, isLoadMore = false)
    }

    fun closeBulkUploadSheet() {
        _isBulkUploadSheetVisible.value = false
        _bulkSearchQuery.value = ""
        _selectedBulkEmployees.value = emptySet()
        _bulkEmployees.value = emptyList()
        bulkPageNumber = 1
        _bulkHasMore.value = true
    }

    fun onBulkSearchQueryChanged(facilitiesId: String, query: String) {
        _bulkSearchQuery.value = query
        fetchBulkEmployees(facilitiesId, isLoadMore = false)
    }

    fun toggleBulkEmployeeSelection(employeeId: Int) {
        val currentSet = _selectedBulkEmployees.value.toMutableSet()
        if (currentSet.contains(employeeId)) {
            currentSet.remove(employeeId)
        } else {
            currentSet.add(employeeId)
        }
        _selectedBulkEmployees.value = currentSet
    }

    fun selectAllBulkEmployees() {
        val allIds = _bulkEmployees.value.map { it.id }.toSet()
        _selectedBulkEmployees.value = allIds
    }

    fun fetchBulkEmployees(facilitiesId: String, isLoadMore: Boolean = false) {
        if (_isBulkLoading.value || (!isLoadMore && !_bulkHasMore.value)) return
        if (isLoadMore && !_bulkHasMore.value) return

        viewModelScope.launch {
            val page = if (isLoadMore) bulkPageNumber + 1 else 1
            _isBulkLoading.value = true

            val result = projectRepository.getEmployeeList(
                groupId = facilitiesId,
                pageNumber = page,
                searchKey = _bulkSearchQuery.value
            )

            when (result) {
                is NetworkResult.Success -> {
                    val newEmployees = result.data
                    val currentList = if (isLoadMore) _bulkEmployees.value else emptyList()
                    _bulkEmployees.value = currentList + newEmployees
                    bulkPageNumber = page
                    _bulkHasMore.value = newEmployees.isNotEmpty()
                    _isBulkLoading.value = false
                }
                is NetworkResult.Error -> {
                    _isBulkLoading.value = false
                    _uiState.value = CreateToolBoxTalkUiState.Error(result.message ?: "Failed to fetch employees")
                }
            }
        }
    }

    fun getSelectedEmployeesList(): List<ToolBoxAttendeeRequest> {
        val selectedEmployees = _bulkEmployees.value.filter { _selectedBulkEmployees.value.contains(it.id) }
        return selectedEmployees.map {
            ToolBoxAttendeeRequest(
                id = it.id,
                employeeCode = it.employeeCode ?: "",
                employeeName = it.employeeName ?: "",
                companyName = it.companyName ?: "",
                profession = it.profession ?: ""
            )
        }
    }

    fun createToolBoxTalk(
        selectedDateMillis: Long?,
        startTimeStr: String,
        endTimeStr: String,
        topic: String,
        discussionPoints: List<String>,
        attendees: List<ToolBoxAttendeeRequest>,
        facilitiesId: String?,
        images: List<ToolBoxTalkImageRequest>?
    ) {
        if (selectedDateMillis == null) {
            _uiState.value = CreateToolBoxTalkUiState.Error("Date is required")
            return
        }
        if (startTimeStr.isBlank() || endTimeStr.isBlank()) {
            _uiState.value = CreateToolBoxTalkUiState.Error("Start and End Times are required")
            return
        }
        if (topic.isBlank()) {
            _uiState.value = CreateToolBoxTalkUiState.Error("Topic is required")
            return
        }

        viewModelScope.launch {
            _uiState.value = CreateToolBoxTalkUiState.Loading

            // 1. Format date (yyyy-MM-dd HH:mm:ss)
            val dateFormatted = formatDateToApi(selectedDateMillis)

            // 2. Format start & end times (HH:mm:ss)
            val formattedStartTime = formatTimeToApi(startTimeStr)
            val formattedEndTime = formatTimeToApi(endTimeStr)

            // 3. Format createdAt (yyyy-MM-dd HH:mm:ss)
            val currentInstant = kotlin.time.Clock.System.now()
            val currentLocalDateTime = currentInstant.toLocalDateTime(TimeZone.currentSystemDefault())
            val formattedCreatedAt = "${currentLocalDateTime.year}-${currentLocalDateTime.monthNumber.toString().padStart(2, '0')}-${currentLocalDateTime.dayOfMonth.toString().padStart(2, '0')} ${currentLocalDateTime.hour.toString().padStart(2, '0')}:${currentLocalDateTime.minute.toString().padStart(2, '0')}:${currentLocalDateTime.second.toString().padStart(2, '0')}"

            // 4. Build request parameters
            val discussionRequests = discussionPoints.filter { it.isNotBlank() }.mapIndexed { idx, point ->
                DiscussionPointRequest(id = idx + 1, point = point)
            }

            val request = CreateToolBoxTalkRequest(
                date = dateFormatted,
                startTime = formattedStartTime,
                endTime = formattedEndTime,
                topic = topic,
                discussionPoints = discussionRequests,
                attendees = attendees,
                createdAt = formattedCreatedAt,
                facilitiesId = facilitiesId,
                images = images?.filter { it.image.isNotBlank() } ?: emptyList(),
                reportedBy = logginedUser?.name ?: ""
            )

            when (val result = repository.createToolBoxTalk(request)) {
                is NetworkResult.Success -> {
                    _uiState.value = CreateToolBoxTalkUiState.Success
                }
                is NetworkResult.Error -> {
                    _uiState.value = CreateToolBoxTalkUiState.Error(result.message)
                }
            }
        }
    }

    private fun formatDateToApi(millis: Long): String {
        val instant = Instant.fromEpochMilliseconds(millis)
        val tz = TimeZone.UTC
        val dt = instant.toLocalDateTime(tz)
        val month = dt.monthNumber.toString().padStart(2, '0')
        val day = dt.dayOfMonth.toString().padStart(2, '0')
        return "${dt.year}-$month-$day 00:00:00"
    }

    private fun formatTimeToApi(timeStr: String): String {
        return try {
            val cleanStr = timeStr.replace(" ", "") // e.g. "02:30PM" or "14:30"
            val isPm = cleanStr.endsWith("PM", ignoreCase = true)
            val isAm = cleanStr.endsWith("AM", ignoreCase = true)
            
            val timePart = if (isPm || isAm) {
                cleanStr.substring(0, cleanStr.length - 2)
            } else {
                cleanStr
            }
            
            val parts = timePart.split(":")
            var hour = parts[0].toInt()
            val minute = parts[1].toInt()
            
            if (isPm && hour < 12) {
                hour += 12
            } else if (isAm && hour == 12) {
                hour = 0
            }
            
            val hourStr = hour.toString().padStart(2, '0')
            val minuteStr = minute.toString().padStart(2, '0')
            "$hourStr:$minuteStr:00"
        } catch (e: Exception) {
            "00:00:00"
        }
    }
}
