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
import androidx.compose.ui.text.font.FontStyle
import org.example.project.typography.textStyle
import org.example.project.ui.components.*
import org.example.project.utilites.*
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.jetbrains.compose.resources.stringResource
import instaresolv.shared.generated.resources.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePreTaskScreen(
    onBackClicked: () -> Unit,
    isFromDraft: Boolean = false,
    draftId: Long = -1L
) {
    val viewModel: CreatePreTaskViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val showExitPopup = remember { mutableStateOf(false) }
    val showDraftSuccessDialog = remember { mutableStateOf(false) }
    val showOutDatedDraftPopUp = remember { mutableStateOf(false) }

    LaunchedEffect(isFromDraft, draftId) {
        viewModel.initialize(isFromDraft, draftId)
    }

    LaunchedEffect(uiState.publishSuccess) {
        if (uiState.publishSuccess) {
            onBackClicked()
        }
    }

    LaunchedEffect(uiState.isDraftOutdated) {
        if (uiState.isDraftOutdated) {
            showOutDatedDraftPopUp.value = true
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
                NavigationBackIcon(onClick = {
                    showExitPopup.value = true
                })
                Text(
                    text = stringResource(Res.string.createPreTaskBriefing),
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 22.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        AppBorderButton(
                            title = stringResource(Res.string.saveAsDraft),
                            onClick = {
                                val projectJson = uiState.selectedProject?.let { Json.encodeToString(it) }
                                val contentsJson = Json.encodeToString(uiState.contents)
                                val questionsJson = Json.encodeToString(uiState.questions)
                                val questionAnswersJson = Json.encodeToString(uiState.questionAnswers)
                                val customQuestionsJson = Json.encodeToString(uiState.customQuestions)
                                val attendeesJson = Json.encodeToString(uiState.attendees)
                                val evidencesJson = Json.encodeToString(uiState.evidences)
                                val selectedNotifyPersonJson = uiState.selectedNotifyPerson?.let { Json.encodeToString(it) }

                                viewModel.saveLocalDraft(
                                    id = if (isFromDraft) draftId else 0L,
                                    facilitiesId = uiState.selectedProject?.groupId,
                                    projectJson = projectJson,
                                    dateMillis = uiState.dateMillis,
                                    startTime = uiState.startTime,
                                    endTime = uiState.endTime,
                                    msraReference = uiState.msraReference,
                                    permitReference = uiState.permitReference,
                                    taskTitle = uiState.taskTitle,
                                    stepByStepAccount = uiState.stepByStepAccount,
                                    contentsJson = contentsJson,
                                    questionsJson = questionsJson,
                                    questionAnswersJson = questionAnswersJson,
                                    customQuestionsJson = customQuestionsJson,
                                    attendeesJson = attendeesJson,
                                    evidencesJson = evidencesJson,
                                    selectedNotifyPersonJson = selectedNotifyPersonJson,
                                    onSuccess = {
                                        showDraftSuccessDialog.value = true
                                    }
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                        AppPrimaryButton(
                            title = stringResource(Res.string.publish),
                            onClick = { viewModel.publishPreTask(isDraft = false) },
                            modifier = Modifier.weight(1f),
                            isLoading = uiState.isPublishing,
                            enabled = !uiState.isDraftOutdated
                        )
                    }
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
                        title = stringResource(Res.string.facilityProject),
                        selectedProject = uiState.selectedProject,
                        onProjectSelected = viewModel::onProjectSelected,
                        isMandatory = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    AppTextField(
                        value = uiState.taskTitle,
                        onValueChange = viewModel::onTaskTitleChanged,
                        title = stringResource(Res.string.taskTitle),
                        placeholder = stringResource(Res.string.enterTaskTitle),
                        isMandatory = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    AppTextField(
                        value = uiState.reportedBy?.name ?: "",
                        onValueChange = {},
                        title = stringResource(Res.string.reportedBy),
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
                        text = stringResource(Res.string.selectDate),
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
                                text = stringResource(Res.string.txt_0000),
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
                                text = stringResource(Res.string.txt_0000),
                                selectedTime = uiState.endTime,
                                onTimeSelected = viewModel::onEndTimeChanged
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    AppTextField(
                        value = uiState.msraReference,
                        onValueChange = viewModel::onMsraChanged,
                        title = stringResource(Res.string.msraReference),
                        placeholder = stringResource(Res.string.enterReference)
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    AppTextField(
                        value = uiState.permitReference,
                        onValueChange = viewModel::onPermitChanged,
                        title = stringResource(Res.string.permitReference),
                        placeholder = stringResource(Res.string.enterReference)
                    )

                    if (uiState.selectedProject != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        AppUserDropdown(
                            title = stringResource(Res.string.sendNotificationTo),
                            selectedUser = null,
                            onUserSelected = { user ->
                                viewModel.onNotifyPersonSelected(user)
                            },
                            placeholder = stringResource(Res.string.chooseUser),
                            users = uiState.groupUsers
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = stringResource(Res.string.topicsOfDiscussion).uppercase(),
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
                        text = stringResource(Res.string.others),
                        style = textStyle(12.sp, FontWeight.Bold),
                        color = Color(0xFF1E5BB2)
                    )
                    uiState.customQuestions.forEach { customQ ->
                        Column(modifier = Modifier.padding(bottom = 5.dp)) {
                            AppTextField(
                                value = customQ.title,
                                onValueChange = { viewModel.updateCustomQuestionTitle(customQ.id, it) },
                                title = "",
                                placeholder = stringResource(Res.string.enterQuestion)
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
                            contentDescription = stringResource(Res.string.add),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(Res.string.addNew),
                            style = textStyle(12.sp, FontWeight.Medium),
                            color = AppColors.Primary
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    AppMultilineTextField(
                        value = uiState.stepByStepAccount,
                        onValueChange = viewModel::onStepByStepAccountChanged,
                        title = stringResource(Res.string.stepByStepAccountOfTodaysTask),
                        placeholder = stringResource(Res.string.enterHere)
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = stringResource(Res.string.attendees),
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
                        onUploadEmployeesClick = { viewModel.openBulkUploadSheet() }
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = stringResource(Res.string.attendeesEvidence),
                        style = textStyle(12.sp, FontWeight.Bold),
                        color = Color(0xFF1E5BB2)
                    )

                    uiState.evidences.forEachIndexed { index, evidence ->
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = stringResource(Res.string.uploadImage), style = textStyle(12.sp, FontWeight.Medium))
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
                            contentDescription = stringResource(Res.string.add),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(Res.string.addImage),
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
            title = stringResource(Res.string.success),
            description = "PreTask Briefing Created Successfully.",
            buttonText = "OK",
            onDismiss = {
                viewModel.clearSuccess()
                onBackClicked()
            }
        )
    }

    if (showDraftSuccessDialog.value) {
        AppStatusDialog(
            visible = true,
            title = stringResource(Res.string.success),
            description = "PreTask Briefing draft saved successfully.",
            buttonText = "OK",
            onDismiss = {
                showDraftSuccessDialog.value = false
                onBackClicked()
            }
        )
    }

    if (uiState.isDraftOutdated) {
        AppErrorDialog(
            visible = showOutDatedDraftPopUp.value,
            title = "Alert",
            description = "Questions have been updated. Please recreate the draft to publish.",
            buttonText = "OK",
            onDismiss = {
                showOutDatedDraftPopUp.value = false
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
}

@Composable
fun QuestionRow(
    title: String,
    selectedAnswer: String?,
    onAnswerSelected: (String) -> Unit,
    remarks: String = "",
    onRemarksChanged: ((String) -> Unit)? = null
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
        if (onRemarksChanged != null) {
            Spacer(modifier = Modifier.height(8.dp))
            org.example.project.utilites.AppTextField(
                value = remarks,
                onValueChange = onRemarksChanged,
                title = stringResource(Res.string.remarksOptional),
                placeholder = stringResource(Res.string.enterRemarks)
            )
        }
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
