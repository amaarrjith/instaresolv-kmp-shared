package org.example.project.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_add
import instaresolv.shared.generated.resources.ic_trash
import org.example.project.colors.AppColors
import org.example.project.data.model.AttendeeRequest
import org.example.project.data.model.ToolBoxAttendeeRequest
import org.example.project.data.model.ToolBoxTalkImageRequest
import org.example.project.typography.textStyle
import org.example.project.ui.components.AddEmployeeBlock
import org.example.project.ui.components.AppDatePicker
import org.example.project.ui.components.AppImageCreateBox
import org.example.project.ui.components.AppProjectDropdown
import org.example.project.ui.components.AppTimePicker
import org.example.project.ui.components.AppStatusDialog
import org.example.project.ui.components.BulkEmployeeUploadSheet
import org.example.project.utilites.AppBorderButton
import org.example.project.utilites.AppPrimaryButton
import org.example.project.utilites.AppTextField
import org.example.project.utilites.NavigationBackIcon
import org.example.project.utilites.ToastHost
import org.example.project.utilites.ToastType
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.jetbrains.compose.resources.stringResource
import instaresolv.shared.generated.resources.*
import org.example.project.ui.components.AppExitPopup
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import org.example.project.data.settings.parseTimeToMinutes
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateToolBoxTalkScreen(
    onBackClicked: () -> Unit,
    isFromDraft: Boolean = false,
    draftId: Long = -1L
) {
    val viewModel: CreateToolBoxTalkViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = androidx.compose.ui.platform.LocalFocusManager.current
    val showSuccessDialog = remember { mutableStateOf(false) }
    val showDraftSuccessDialog = remember { mutableStateOf(false) }

    var selectedProject by remember { mutableStateOf<org.example.project.data.model.Project?>(null) }
    var facilitiesId by remember { mutableStateOf<String?>(null) }
    var topic by remember { mutableStateOf("") }
    
    // Date & Times
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }

    // Discussion Points - Initial 4 points, user can add more
    val discussionPoints = remember { mutableStateListOf("", "", "", "") }

    // Attendees
    val attendees = remember { mutableStateListOf<ToolBoxAttendeeRequest>() }

    // Images
    val images = remember { mutableStateListOf(org.example.project.ui.screens.ObservationImage()) }

    var showErrorToast by remember { mutableStateOf<String?>(null) }
    val showExitPopup = remember { mutableStateOf(false) }
    val topicRequiredError = stringResource(Res.string.enter_field, stringResource(Res.string.topic))
    val selectDateError = stringResource(Res.string.selectDate)
    val selectStartTimeError = stringResource(Res.string.error_select_start_time)
    val endTimeGreaterError = stringResource(Res.string.error_end_time_greater)
    val cannotSelectFutureTimeError = stringResource(Res.string.error_cannot_select_future_time)
    val atLeastOneDiscussionPointError = stringResource(Res.string.error_at_least_one_discussion_point)
    val fillPreviousDiscussionPointsError = stringResource(Res.string.error_fill_previous_discussion_points)
    val atLeastOneEmployeeError = stringResource(Res.string.error_at_least_one_employee)

    val isToday = remember(selectedDateMillis) {
        selectedDateMillis?.let { millis ->
            val selectedDate = Instant.fromEpochMilliseconds(millis)
                .toLocalDateTime(TimeZone.UTC).date
            val today = Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault()).date
            selectedDate == today
        } ?: false
    }

    LaunchedEffect(isFromDraft, draftId) {
        if (isFromDraft && draftId != -1L) {
            viewModel.draftId = draftId
            val draft = viewModel.getDraftById(draftId)
            if (draft != null) {
                topic = draft.topic.orEmpty()
                selectedDateMillis = draft.dateMillis

                val isDraftToday = draft.dateMillis?.let { millis ->
                    val selectedDate = Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.UTC).date
                    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                    selectedDate == today
                } ?: false

                val currentMinutesNow = run {
                    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                    now.hour * 60 + now.minute
                }

                val sanitizedStartTime = if (isDraftToday && !draft.startTime.isNullOrBlank() && parseTimeToMinutes(draft.startTime) > currentMinutesNow) {
                    ""
                } else {
                    draft.startTime ?: ""
                }

                val sanitizedEndTime = if (
                    sanitizedStartTime.isBlank() ||
                    draft.endTime.isNullOrBlank() ||
                    parseTimeToMinutes(draft.endTime) <= parseTimeToMinutes(sanitizedStartTime) ||
                    (isDraftToday && parseTimeToMinutes(draft.endTime) > currentMinutesNow)
                ) {
                    ""
                } else {
                    draft.endTime ?: ""
                }

                startTime = sanitizedStartTime
                endTime = sanitizedEndTime
                facilitiesId = draft.facilitiesId?.toString()

                val project = draft.projectJson?.let {
                    try {
                        Json.decodeFromString<org.example.project.data.model.Project>(it)
                    } catch (e: Exception) {
                        null
                    }
                }
                selectedProject = project

                val localPoints = draft.discussionPointsJson?.let {
                    try {
                        Json.decodeFromString<List<String>>(it)
                    } catch (e: Exception) {
                        null
                    }
                }
                if (!localPoints.isNullOrEmpty()) {
                    discussionPoints.clear()
                    discussionPoints.addAll(localPoints)
                }

                val localAttendees = draft.attendeesJson?.let {
                    try {
                        Json.decodeFromString<List<ToolBoxAttendeeRequest>>(it)
                    } catch (e: Exception) {
                        null
                    }
                }
                if (!localAttendees.isNullOrEmpty()) {
                    attendees.clear()
                    attendees.addAll(localAttendees)
                }

                val localImages = draft.imagesJson?.let {
                    try {
                        Json.decodeFromString<List<org.example.project.ui.screens.ObservationImage>>(it)
                    } catch (e: Exception) {
                        null
                    }
                }
                if (!localImages.isNullOrEmpty()) {
                    images.clear()
                    images.addAll(localImages)
                }
            }
        }
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            is CreateToolBoxTalkUiState.Success -> {
                viewModel.resetState()
                showSuccessDialog.value = true
            }
            is CreateToolBoxTalkUiState.Error -> {
                showErrorToast = (uiState as CreateToolBoxTalkUiState.Error).message
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .clickable(
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                indication = null
            ) {
                focusManager.clearFocus()
            },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = Color.White,
        topBar = {
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(vertical = 10.dp)
                    .padding(end = 22.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavigationBackIcon(onClick = {
                    showExitPopup.value = true
                })
                Text(
                    text = stringResource(Res.string.createToolboxTalk),
                    style = textStyle(
                        size = 14.sp,
                        weight = FontWeight.Bold
                    ),
                    color = AppColors.Black
                )
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 8.dp)
                    .background(Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 22.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AppBorderButton(
                        title = stringResource(Res.string.saveAsDraft),
                        onClick = {
                            val imagesJson = try {
                                Json.encodeToString(images.toList())
                            } catch (e: Exception) {
                                "[]"
                            }
                            val projectJson = selectedProject?.let {
                                try {
                                    Json.encodeToString(it)
                                } catch (e: Exception) {
                                    null
                                }
                            }
                            val discussionPointsJson = try {
                                Json.encodeToString(discussionPoints.toList())
                            } catch (e: Exception) {
                                "[]"
                            }
                            val attendeesJson = try {
                                Json.encodeToString(attendees.toList())
                            } catch (e: Exception) {
                                "[]"
                            }
                            viewModel.saveLocalDraft(
                                id = if (isFromDraft) draftId else 0L,
                                facilitiesId = selectedProject?.groupId,
                                projectJson = projectJson,
                                dateMillis = selectedDateMillis,
                                startTime = startTime,
                                endTime = endTime,
                                topic = topic,
                                discussionPointsJson = discussionPointsJson,
                                attendeesJson = attendeesJson,
                                imagesJson = imagesJson,
                                onSuccess = {
                                    showDraftSuccessDialog.value = true
                                }
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                    AppPrimaryButton(
                        title = stringResource(Res.string.save),
                        onClick = {
                            if (topic.isBlank()) {
                                showErrorToast = topicRequiredError
                                return@AppPrimaryButton
                            }
                            if (selectedDateMillis == null) {
                                showErrorToast = selectDateError
                                return@AppPrimaryButton
                            }
                            if (startTime.isBlank()) {
                                showErrorToast = selectStartTimeError
                                return@AppPrimaryButton
                            }
                            if (endTime.isBlank()) {
                                showErrorToast = endTimeGreaterError
                                return@AppPrimaryButton
                            }
                            if (parseTimeToMinutes(endTime) <= parseTimeToMinutes(startTime)) {
                                showErrorToast = endTimeGreaterError
                                return@AppPrimaryButton
                            }
                            if (isToday) {
                                val currentMinutes = run {
                                    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                                    now.hour * 60 + now.minute
                                }
                                if (parseTimeToMinutes(startTime) > currentMinutes || parseTimeToMinutes(endTime) > currentMinutes) {
                                    showErrorToast = cannotSelectFutureTimeError
                                    return@AppPrimaryButton
                                }
                            }
                            if (discussionPoints.none { it.isNotBlank() }) {
                                showErrorToast = atLeastOneDiscussionPointError
                                return@AppPrimaryButton
                            }
                            if (attendees.isEmpty()) {
                                showErrorToast = atLeastOneEmployeeError
                                return@AppPrimaryButton
                            }
                            val imageRequests = images.filter { it.imageUrl?.isNotBlank() == true }.map {
                                ToolBoxTalkImageRequest(
                                    image = it.imageUrl ?: "",
                                    description = it.description
                                )
                            }
                            viewModel.createToolBoxTalk(
                                selectedDateMillis = selectedDateMillis,
                                startTimeStr = startTime,
                                endTimeStr = endTime,
                                topic = topic,
                                discussionPoints = discussionPoints.toList(),
                                attendees = attendees.toList(),
                                facilitiesId = facilitiesId,
                                images = imageRequests.takeIf { it.isNotEmpty() }
                            )
                        },
                        modifier = Modifier.weight(1f),
                        isLoading = uiState is CreateToolBoxTalkUiState.Loading,
                        enabled = uiState !is CreateToolBoxTalkUiState.Loading,
                        fillMaxWidth = false
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()).padding(bottom = 20.dp).padding(horizontal = 22.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                AppProjectDropdown(
                    onProjectSelected = { project ->
                        selectedProject = project
                        facilitiesId = project?.groupId.toString()
                    },
                    selectedProject = selectedProject
                )

                AppTextField(
                    isMandatory = true,
                    value = viewModel.logginedUser?.name ?: "",
                    onValueChange = {},
                    title = stringResource(Res.string.reportedBy),
                    placeholder = "",
                    readOnly = true,
                    enabled = false
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = buildAnnotatedString {
                            append("${stringResource(Res.string.date)} ")
                            withStyle(SpanStyle(color = Color.Red)) { append("*") }
                        },
                        style = textStyle(12.sp, FontWeight.SemiBold)
                    )
                    AppDatePicker(
                        text = stringResource(Res.string.selectDate),
                        selectedDateMillis = selectedDateMillis,
                        onDateSelected = {
                            selectedDateMillis = it
                            startTime = ""
                            endTime = ""
                        },
                        restrictFutureDates = true
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                append("${stringResource(Res.string.startTime)} ")
                                withStyle(SpanStyle(color = Color.Red)) { append("*") }
                            },
                            style = textStyle(12.sp, FontWeight.SemiBold)
                        )
                        Box {
                            AppTimePicker(
                                text = stringResource(Res.string.txt_0000),
                                selectedTime = startTime,
                                onTimeSelected = { time ->
                                    endTime = ""
                                    startTime = time
                                },
                                enabled = selectedDateMillis != null,
                                restrictFutureTime = isToday
                            )
                            if (selectedDateMillis == null) {
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .clickable { showErrorToast = selectDateError }
                                )
                            }
                        }
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                append("${stringResource(Res.string.endTime)} ")
                                withStyle(SpanStyle(color = Color.Red)) { append("*") }
                            },
                            style = textStyle(12.sp, FontWeight.SemiBold)
                        )
                        Box {
                            AppTimePicker(
                                text = stringResource(Res.string.txt_0000),
                                selectedTime = endTime,
                                onTimeSelected = { newEndTime ->
                                    if (startTime.isNotBlank() && parseTimeToMinutes(newEndTime) <= parseTimeToMinutes(startTime)) {
                                        showErrorToast = endTimeGreaterError
                                    } else {
                                        endTime = newEndTime
                                    }
                                },
                                enabled = selectedDateMillis != null && startTime.isNotBlank(),
                                restrictFutureTime = isToday,
                                minTime = startTime,
                                minTimeErrorMessage = endTimeGreaterError
                            )
                            if (selectedDateMillis == null) {
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .clickable { showErrorToast = selectDateError }
                                )
                            } else if (startTime.isBlank()) {
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .clickable { showErrorToast = selectStartTimeError }
                                )
                            }
                        }
                    }
                }

                AppTextField(
                    isMandatory = true,
                    value = topic,
                    onValueChange = { topic = it },
                    title = stringResource(Res.string.topic),
                    placeholder = stringResource(Res.string.enterTopic)
                )

                // Discussion Points
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = buildAnnotatedString {
                            append("${stringResource(Res.string.discussionPoints)} ")
                            withStyle(SpanStyle(color = Color.Red)) { append("*") }
                        },
                        style = textStyle(size = 14.sp, weight = FontWeight.Bold),
                        color = AppColors.Black
                    )

                    discussionPoints.forEachIndexed { index, point ->
                        AppTextField(
                            value = point,
                            onValueChange = { discussionPoints[index] = it },
                            title = stringResource(Res.string.point_index, index + 1),
                            placeholder = stringResource(Res.string.enterDiscussionPoint)
                        )
                    }

                    TextButton(
                        onClick = {
                            if (discussionPoints.any { it.isBlank() }) {
                                showErrorToast = fillPreviousDiscussionPointsError
                            } else {
                                discussionPoints.add("")
                            }
                        },
                        modifier = Modifier.align(Alignment.Start)
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_add),
                            contentDescription = stringResource(Res.string.addPoint),
                            modifier = Modifier.size(15.dp),
                            tint = AppColors.Primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(Res.string.addPoint),
                            style = textStyle(
                                size = 12.sp,
                                weight = FontWeight.SemiBold
                            ),
                            color = AppColors.Primary
                        )
                    }
                }

                // Attendees Section
                Text(
                    text = buildAnnotatedString {
                        append("${stringResource(Res.string.attendeesList)} ")
                        withStyle(SpanStyle(color = Color.Red)) { append("*") }
                    },
                    style = textStyle(size = 14.sp, weight = FontWeight.Bold),
                    color = AppColors.Black
                )

                if (attendees.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFE5E5E5), RoundedCornerShape(8.dp))
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier
                                        .background(Color(0xFFF5F5F5))
                                        .padding(horizontal = 16.dp, vertical = 12.dp)
                                ) {
                                    Text(stringResource(Res.string.employee_and_id), modifier = Modifier.width(200.dp), style = textStyle(size = 12.sp, weight = FontWeight.Medium), color = AppColors.TextGray)
                                    Text(stringResource(Res.string.companyName), modifier = Modifier.width(150.dp), style = textStyle(size = 12.sp, weight = FontWeight.Medium), color = AppColors.TextGray)
                                    Text(stringResource(Res.string.profession), modifier = Modifier.width(150.dp), style = textStyle(size = 12.sp, weight = FontWeight.Medium), color = AppColors.TextGray)
                                    Text(stringResource(Res.string.action), modifier = Modifier.width(60.dp), style = textStyle(size = 12.sp, weight = FontWeight.Medium), color = AppColors.TextGray)
                                }

                                attendees.forEachIndexed { index, attendee ->
                                    Row(
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.width(200.dp)) {
                                            Text(attendee.employeeName.orEmpty(), style = textStyle(size = 13.sp, weight = FontWeight.SemiBold), color = AppColors.Black)
                                            Text(attendee.employeeCode.orEmpty(), style = textStyle(size = 11.sp, weight = FontWeight.Normal), color = AppColors.TextGray)
                                        }
                                        Text(attendee.companyName.orEmpty(), modifier = Modifier.width(150.dp), style = textStyle(size = 13.sp, weight = FontWeight.Normal), color = AppColors.Black)
                                        Text(attendee.profession.orEmpty(), modifier = Modifier.width(150.dp), style = textStyle(size = 13.sp, weight = FontWeight.Normal), color = AppColors.Black)
                                        Box(modifier = Modifier.width(60.dp), contentAlignment = Alignment.Center) {
                                            Image(
                                                painter = painterResource(Res.drawable.ic_trash),
                                                contentDescription = stringResource(Res.string.removeAttendee),
                                                modifier = Modifier.size(20.dp).clickable { attendees.removeAt(index) }
                                            )
                                        }
                                    }
                                    if (index < attendees.lastIndex) {
                                        HorizontalDivider(color = Color(0xFFF5F5F5), modifier = Modifier.padding(horizontal = 16.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                // Add Attendee Section
                AddEmployeeBlock(
                    onAddEmployee = { injured ->
                        attendees.add(
                            ToolBoxAttendeeRequest(
                                id = -1,
                                employeeCode = injured.employeeCode,
                                employeeName = injured.employeeName,
                                companyName = injured.companyName,
                                profession = injured.profession
                            )
                        )
                    },
                    onError = { showErrorToast = it },
                    onUploadEmployeesClick = {
                        viewModel.openBulkUploadSheet()
                    }
                )

                // Attendance Evidence
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.attendanceEvidence),
                        style = textStyle(size = 14.sp, weight = FontWeight.Bold),
                        color = AppColors.Black
                    )

                    images.forEachIndexed { index, img ->
                        Text(
                            text = stringResource(Res.string.upload_image_index, index + 1),
                            style = textStyle(size = 12.sp, weight = FontWeight.SemiBold),
                            color = AppColors.Black
                        )
                        AppImageCreateBox(
                            imageUrl = img.imageUrl,
                            description = img.description,
                            onDescriptionChange = { newDesc ->
                                images[index] = img.copy(description = newDesc)
                            },
                            onImageUploaded = { newUrl ->
                                images[index] = img.copy(imageUrl = newUrl)
                            },
                            onRemoveImageClick = {
                                if (images.size > 1) {
                                    images.removeAt(index)
                                } else {
                                    images[index] = org.example.project.ui.screens.ObservationImage()
                                }
                            }
                        )
                    }

                    if (images.size < 6) {
                        TextButton(
                            onClick = { images.add(org.example.project.ui.screens.ObservationImage()) },
                            modifier = Modifier.align(Alignment.Start)
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_add),
                                contentDescription = stringResource(Res.string.addImage),
                                modifier = Modifier.size(15.dp),
                                tint = AppColors.Primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(Res.string.addImage),
                                style = textStyle(
                                    size = 12.sp,
                                    weight = FontWeight.SemiBold
                                ),
                                color = AppColors.Primary
                            )
                        }
                    }
                }
            }

            ToastHost(
                visible = showErrorToast != null,
                message = showErrorToast.orEmpty(),
                onDismiss = { showErrorToast = null },
                type = ToastType.Error
            )
        }

        if (showSuccessDialog.value) {
            AppStatusDialog(
                visible = showSuccessDialog.value,
                title = stringResource(Res.string.success),
                description = stringResource(Res.string.toolbox_talk_created_success),
                buttonText = stringResource(Res.string.close),
                onDismiss = {
                    showSuccessDialog.value = false
                    onBackClicked()
                }
            )
        }
        if (showDraftSuccessDialog.value) {
            AppStatusDialog(
                visible = showDraftSuccessDialog.value,
                title = stringResource(Res.string.success),
                description = stringResource(Res.string.toolbox_talk_draft_saved_success),
                buttonText = stringResource(Res.string.close),
                onDismiss = {
                    showDraftSuccessDialog.value = false
                    onBackClicked()
                }
            )
        }


        AppExitPopup(
            visible = showExitPopup.value,
            onPrimaryClick = {
                showExitPopup.value = false
                onBackClicked()
            },
            onSecondaryClick = {
                showExitPopup.value = false
            },
            onDismiss = {
                showExitPopup.value = false
            }
        )

        // Bulk Employee Upload Sheet
        val isBulkUploadSheetVisible by viewModel.isBulkUploadSheetVisible.collectAsState()
        val bulkEmployees by viewModel.bulkEmployees.collectAsState()
        val selectedBulkEmployees by viewModel.selectedBulkEmployees.collectAsState()
        val bulkSearchQuery by viewModel.bulkSearchQuery.collectAsState()
        val isBulkLoading by viewModel.isBulkLoading.collectAsState()
        val bulkHasMore by viewModel.bulkHasMore.collectAsState()

        if (isBulkUploadSheetVisible) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.closeBulkUploadSheet() },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ) {
                BulkEmployeeUploadSheet(
                    searchQuery = bulkSearchQuery,
                    onSearchQueryChange = { query ->
                        viewModel.onBulkSearchQueryChanged(facilitiesId ?: "", query)
                    },
                    employees = bulkEmployees,
                    selectedEmployeeIds = selectedBulkEmployees,
                    onToggleSelection = { employeeId ->
                        viewModel.toggleBulkEmployeeSelection(employeeId)
                    },
                    onSelectAll = {
                        viewModel.selectAllBulkEmployees()
                    },
                    onAddEmployees = {
                        val selected = viewModel.getSelectedEmployeesList()
                        attendees.addAll(selected)
                        viewModel.closeBulkUploadSheet()
                    },
                    onLoadMore = {
                        viewModel.fetchBulkEmployees(isLoadMore = true)
                    },
                    isLoading = isBulkLoading,
                    hasMore = bulkHasMore
                )
            }
        }
    }
}
