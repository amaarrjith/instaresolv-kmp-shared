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
import androidx.compose.ui.text.font.FontWeight
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateToolBoxTalkScreen(
    onBackClicked: () -> Unit
) {
    val viewModel: CreateToolBoxTalkViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = androidx.compose.ui.platform.LocalFocusManager.current
    val showSuccessDialog = remember { mutableStateOf(false) }

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
                NavigationBackIcon(onBackClicked)
                Text(
                    text = "CREATE - TOOLBOX TALK",
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
                        title = "Save as Draft",
                        onClick = {
                            if (topic.isBlank()) {
                                showErrorToast = "Topic is required"
                                return@AppBorderButton
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
                        modifier = Modifier.weight(1f)
                    )
                    AppPrimaryButton(
                        title = "Save",
                        onClick = {
                            if (topic.isBlank()) {
                                showErrorToast = "Topic is required"
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
                .padding(horizontal = 22.dp)
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()).padding(bottom = 20.dp),
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
                    title = "Reported By",
                    placeholder = "",
                    readOnly = true,
                    enabled = false
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Date *",
                        style = textStyle(size = 12.sp, weight = FontWeight.SemiBold),
                        color = AppColors.Black
                    )
                    AppDatePicker(
                        text = "Select Date",
                        selectedDateMillis = selectedDateMillis,
                        onDateSelected = { selectedDateMillis = it }
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
                            text = "Start Time *",
                            style = textStyle(size = 12.sp, weight = FontWeight.SemiBold),
                            color = AppColors.Black
                        )
                        AppTimePicker(
                            text = "00 : 00",
                            selectedTime = startTime,
                            onTimeSelected = { startTime = it }
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "End Time *",
                            style = textStyle(size = 12.sp, weight = FontWeight.SemiBold),
                            color = AppColors.Black
                        )
                        AppTimePicker(
                            text = "00 : 00",
                            selectedTime = endTime,
                            onTimeSelected = { endTime = it }
                        )
                    }
                }

                AppTextField(
                    isMandatory = true,
                    value = topic,
                    onValueChange = { topic = it },
                    title = "Topic",
                    placeholder = "Enter Topic"
                )

                // Discussion Points
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Discussion Points",
                        style = textStyle(size = 14.sp, weight = FontWeight.Bold),
                        color = AppColors.Black
                    )

                    discussionPoints.forEachIndexed { index, point ->
                        AppTextField(
                            value = point,
                            onValueChange = { discussionPoints[index] = it },
                            title = "Point ${index + 1}",
                            placeholder = "Enter Discussion Point"
                        )
                    }

                    TextButton(
                        onClick = { discussionPoints.add("") },
                        modifier = Modifier.align(Alignment.Start)
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_add),
                            contentDescription = "Add Point",
                            modifier = Modifier.size(15.dp),
                            tint = AppColors.Primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Add Point",
                            style = textStyle(
                                size = 12.sp,
                                weight = FontWeight.SemiBold
                            ),
                            color = AppColors.Primary
                        )
                    }
                }

                // Attendees List View
                if (attendees.isNotEmpty()) {
                    Text(
                        text = "Attendees List",
                        style = textStyle(size = 14.sp, weight = FontWeight.Bold),
                        color = AppColors.Black
                    )
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
                                    Text("Employee & ID", modifier = Modifier.width(200.dp), style = textStyle(size = 12.sp, weight = FontWeight.Medium), color = AppColors.TextGray)
                                    Text("Company Name", modifier = Modifier.width(150.dp), style = textStyle(size = 12.sp, weight = FontWeight.Medium), color = AppColors.TextGray)
                                    Text("Profession", modifier = Modifier.width(150.dp), style = textStyle(size = 12.sp, weight = FontWeight.Medium), color = AppColors.TextGray)
                                    Text("Action", modifier = Modifier.width(60.dp), style = textStyle(size = 12.sp, weight = FontWeight.Medium), color = AppColors.TextGray)
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
                                                contentDescription = "Remove Attendee",
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
                    isProjectSelected = selectedProject != null,
                    onUploadEmployeesClick = {
                        viewModel.openBulkUploadSheet(facilitiesId)
                    }
                )

                // Attendance Evidence
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Attendance Evidence",
                        style = textStyle(size = 14.sp, weight = FontWeight.Bold),
                        color = AppColors.Black
                    )

                    images.forEachIndexed { index, img ->
                        Text(
                            text = "Upload Image ${index + 1}",
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
                                contentDescription = "Add Image",
                                modifier = Modifier.size(15.dp),
                                tint = AppColors.Primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Add Image",
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
                title = "Success",
                description = "Toolbox Talk created successfully.",
                buttonText = "OK",
                onDismiss = {
                    showSuccessDialog.value = false
                    onBackClicked()
                }
            )
        }

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
                        viewModel.fetchBulkEmployees(facilitiesId ?: "", isLoadMore = true)
                    },
                    isLoading = isBulkLoading,
                    hasMore = bulkHasMore
                )
            }
        }
    }
}
