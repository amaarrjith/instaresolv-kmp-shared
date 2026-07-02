package org.example.project.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_add
import instaresolv.shared.generated.resources.ic_checkbox_off
import instaresolv.shared.generated.resources.ic_checkbox_on
import instaresolv.shared.generated.resources.ic_trash
import org.example.project.colors.AppColors
import org.example.project.data.settings.formatDate
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import org.example.project.typography.textStyle
import org.example.project.ui.components.*
import org.example.project.utilites.*
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePreTaskScreen(
    onBackClicked: () -> Unit
) {
    val viewModel: CreatePreTaskViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    
    LaunchedEffect(uiState.publishSuccess) {
        if (uiState.publishSuccess) {
            onBackClicked()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                focusManager.clearFocus()
            },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = Color.White,
        topBar = {
            Row (
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(vertical = 10.dp)
                    .padding(end = 22.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavigationBackIcon(onBackClicked)
                Text(
                    text = "CREATE - PRE TASK BRIEFING",
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
                        onClick = { viewModel.publishPreTask(isDraft = true) },
                        modifier = Modifier.weight(1f)
                    )
                    AppPrimaryButton(
                        title = "Publish",
                        onClick = { viewModel.publishPreTask(isDraft = false) },
                        modifier = Modifier.weight(1f),
                        isLoading = uiState.isPublishing
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                AppLoader()
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 22.dp)
                        .padding(bottom = 100.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    AppProjectDropdown(
                        title = "Facility / Project",
                        selectedProject = uiState.selectedProject,
                        onProjectSelected = viewModel::onProjectSelected
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    AppTextField(
                        value = uiState.taskTitle,
                        onValueChange = viewModel::onTaskTitleChanged,
                        title = "Task Title",
                        placeholder = "Enter Task Title",
                        isMandatory = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    AppTextField(
                        value = uiState.reportedBy?.name ?: "",
                        onValueChange = {},
                        title = "Reported By",
                        placeholder = "",
                        isMandatory = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = buildAnnotatedString {
                            append("Date ")
                            withStyle(SpanStyle(color = Color.Red)) { append("*") }
                        },
                        style = textStyle(12.sp, FontWeight.SemiBold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    AppDatePicker(
                        text = "Select Date",
                        selectedDateMillis = uiState.dateMillis,
                        onDateSelected = { if (it != null) viewModel.onDateSelected(it) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = buildAnnotatedString {
                                    append("Start Time ")
                                    withStyle(SpanStyle(color = Color.Red)) { append("*") }
                                },
                                style = textStyle(12.sp, FontWeight.SemiBold)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            AppTimePicker(
                                text = "00 : 00",
                                selectedTime = uiState.startTime,
                                onTimeSelected = viewModel::onStartTimeChanged
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = buildAnnotatedString {
                                    append("End Time ")
                                    withStyle(SpanStyle(color = Color.Red)) { append("*") }
                                },
                                style = textStyle(12.sp, FontWeight.SemiBold)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            AppTimePicker(
                                text = "00 : 00",
                                selectedTime = uiState.endTime,
                                onTimeSelected = viewModel::onEndTimeChanged
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    AppTextField(
                        value = uiState.msraReference,
                        onValueChange = viewModel::onMsraChanged,
                        title = "MSRA Reference",
                        placeholder = "Enter Reference"
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    AppTextField(
                        value = uiState.permitReference,
                        onValueChange = viewModel::onPermitChanged,
                        title = "Permit Reference",
                        placeholder = "Enter Reference"
                    )

                    if (uiState.selectedProject != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        AppUserDropdown(
                            title = "Send Notification To",
                            selectedUser = null,
                            onUserSelected = { },
                            placeholder = "Choose User",
                            users = uiState.groupUsers
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Topics of Discussion".uppercase(),
                        style = textStyle(14.sp, FontWeight.Bold),
                        color = AppColors.Primary
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    uiState.contents.forEach { content ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = (content.title ?: "").uppercase(),
                            style = textStyle(12.sp, FontWeight.SemiBold),
                            color = Color(0xFF1E5BB2)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        val contentQuestions = uiState.questions.filter { it.contentId == content.id }
                        contentQuestions.forEach { question ->
                            QuestionRow(
                                title = question.title ?: "",
                                selectedAnswer = uiState.questionAnswers[question.id],
                                onAnswerSelected = { answer -> viewModel.onQuestionAnswered(question.id, answer) }
                            )
                        }
                        HorizontalDivider(
                            color = Color(0xFFE5E5EA),
                            modifier = Modifier.padding(vertical = 14.dp)
                        )
                    }

                    // OTHERS section
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "OTHERS",
                        style = textStyle(12.sp, FontWeight.Bold),
                        color = Color(0xFF1E5BB2)
                    )
                    uiState.customQuestions.forEach { customQ ->
                        Column(modifier = Modifier.padding(bottom = 5.dp)) {
                            AppTextField(
                                value = customQ.title,
                                onValueChange = { viewModel.updateCustomQuestionTitle(customQ.id, it) },
                                title = "",
                                placeholder = "Enter Question"
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            AnswerRadioGroup(
                                selectedAnswer = customQ.answer,
                                onAnswerSelected = { viewModel.updateCustomQuestionAnswer(customQ.id, it) }
                            )
                        }
                    }
                    
                    Row(
                        modifier = Modifier
                            .clickable { viewModel.addCustomQuestion() }
                            .padding(vertical = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.ic_add),
                            contentDescription = "Add",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Add New",
                            style = textStyle(12.sp, FontWeight.Medium),
                            color = AppColors.Primary
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    AppMultilineTextField(
                        value = uiState.stepByStepAccount,
                        onValueChange = viewModel::onStepByStepAccountChanged,
                        title = "Step By Step Account of Today's Task",
                        placeholder = "Enter here"
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "ATTENDEES",
                        style = textStyle(12.sp, FontWeight.Bold),
                        color = Color(0xFF1E5BB2)
                    )
                    
                    if (uiState.attendees.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
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
                                    // Header Row
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

                                    // Data Rows
                                    uiState.attendees.forEachIndexed { index, employee ->
                                        Row(
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.width(200.dp)) {
                                                Text(employee.employeeName, style = textStyle(size = 13.sp, weight = FontWeight.SemiBold), color = AppColors.Black)
                                                Text(employee.employeeCode, style = textStyle(size = 11.sp, weight = FontWeight.Normal), color = AppColors.TextGray)
                                            }
                                            Text(employee.companyName, modifier = Modifier.width(150.dp), style = textStyle(size = 13.sp, weight = FontWeight.Normal), color = AppColors.Black)
                                            Text(employee.profession, modifier = Modifier.width(150.dp), style = textStyle(size = 13.sp, weight = FontWeight.Normal), color = AppColors.Black)
                                            Box(modifier = Modifier.width(60.dp), contentAlignment = Alignment.Center) {
                                                Image(
                                                    painter = painterResource(Res.drawable.ic_trash),
                                                    contentDescription = null,
                                                    modifier = Modifier.size(20.dp).clickable { viewModel.onRemoveAttendee(index) }
                                                )
                                            }
                                        }
                                        if (index < uiState.attendees.lastIndex) {
                                            HorizontalDivider(color = Color(0xFFF5F5F5), modifier = Modifier.padding(horizontal = 16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    AddEmployeeBlock(
                        onAddEmployee = { injured ->
                            viewModel.addAttendee(
                                AttendeeItem(
                                    employeeCode = injured.employeeCode ?: "",
                                    employeeName = injured.employeeName ?: "",
                                    companyName = injured.companyName ?: "",
                                    profession = injured.profession ?: ""
                                )
                            )
                        },
                        onError = { viewModel.setError(it) },
                        isProjectSelected = uiState.selectedProject != null,
                        onUploadEmployeesClick = { viewModel.openBulkUploadSheet() }
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "ATTENDEES EVIDENCE",
                        style = textStyle(12.sp, FontWeight.Bold),
                        color = Color(0xFF1E5BB2)
                    )

                    uiState.evidences.forEachIndexed { index, evidence ->
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Upload Image", style = textStyle(12.sp, FontWeight.Medium))
                        Spacer(modifier = Modifier.height(8.dp))
                        AppImageCreateBox(
                            imageUrl = evidence.imagePath,
                            description = evidence.description,
                            onImageUploaded = { viewModel.updateEvidenceImage(index, it) },
                            onDescriptionChange = { viewModel.updateEvidenceDescription(index, it) },
                            onRemoveImageClick = { viewModel.updateEvidenceImage(index, "") }
                        )
                    }

                    Row(
                        modifier = Modifier
                            .clickable { viewModel.addEvidence() }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.ic_add),
                            contentDescription = "Add",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Add Image",
                            style = textStyle(12.sp, FontWeight.Medium),
                            color = AppColors.Primary
                        )
                    }

                }
            }

            ToastHost(
                visible = uiState.error != null,
                message = uiState.error ?: "",
                onDismiss = viewModel::clearError,
                type = ToastType.Error,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 22.dp)
            )
        }
    }

    if (uiState.isBulkUploadSheetVisible) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.closeBulkUploadSheet() },
            containerColor = Color.White,
            dragHandle = { BottomSheetDefaults.DragHandle() },
        ) {
            BulkEmployeeUploadSheet(
                searchQuery = uiState.bulkSearchQuery,
                onSearchQueryChange = { viewModel.onBulkSearchQueryChanged(it) },
                employees = uiState.bulkEmployees,
                selectedEmployeeIds = uiState.selectedBulkEmployees,
                onToggleSelection = { viewModel.toggleBulkEmployeeSelection(it) },
                onSelectAll = { viewModel.selectAllBulkEmployees() },
                onAddEmployees = { viewModel.onAddBulkEmployees() },
                onLoadMore = { viewModel.fetchBulkEmployees(isLoadMore = true) },
                isLoading = uiState.isBulkLoading,
                hasMore = uiState.bulkHasMore
            )
        }
    }

    if (uiState.publishSuccess) {
        AppStatusDialog(
            visible = true,
            title = "Success",
            description = "PreTask Briefing Created Successfully.",
            buttonText = "OK",
            onDismiss = {
                viewModel.clearSuccess()
                onBackClicked()
            }
        )
    }
}

@Composable
fun QuestionRow(
    title: String,
    selectedAnswer: String?,
    onAnswerSelected: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
        Text(
            text = buildAnnotatedString {
                append(title)
                withStyle(SpanStyle(color = Color.Red)) { append(" *") }
            },
            style = textStyle(13.sp, FontWeight.SemiBold),
            color = AppColors.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        AnswerRadioGroup(
            selectedAnswer = selectedAnswer,
            onAnswerSelected = onAnswerSelected
        )
    }
}

@Composable
fun AnswerRadioGroup(
    selectedAnswer: String?,
    onAnswerSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val options = listOf("Yes", "No", "Not Applicable")
        options.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onAnswerSelected(option) }
            ) {
                Image(
                    painter = painterResource(if (selectedAnswer == option) Res.drawable.ic_checkbox_on else Res.drawable.ic_checkbox_off),
                    contentDescription = option,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = option.uppercase(),
                    style = textStyle(12.sp, FontWeight.Medium),
                    color = AppColors.Black
                )
            }
        }
    }
}
