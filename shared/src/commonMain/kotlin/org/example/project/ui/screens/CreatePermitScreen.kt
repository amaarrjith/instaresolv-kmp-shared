package org.example.project.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.koinInject
import org.example.project.ui.components.AppLoader
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.colors.AppColors
import org.example.project.typography.textStyle
import org.example.project.ui.components.AppDatePicker
import org.example.project.ui.components.AppProjectDropdown
import org.example.project.ui.components.AppSignCreateBox
import org.example.project.ui.components.AppTimePicker
import org.example.project.ui.components.AppUserDropdown
import org.example.project.utilites.AppTextField
import org.example.project.utilites.NavigationBackIcon
import org.example.project.utilites.ToastHost
import org.example.project.utilites.ToastType
import org.jetbrains.compose.resources.stringResource
import instaresolv.shared.generated.resources.*
import org.example.project.ui.components.AppExitPopup

@Composable
fun CreatePermitScreen(
    onBackClicked: () -> Unit = {},
    permitTypeId: Int = -1,
    permitTypeName: String = "",
    isFromDraft: Boolean = false,
    draftId: Long = -1L
) {
    val viewModel: CreatePermitViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()
    var localError by remember { mutableStateOf<String?>(null) }
    val showExitPopup = remember { mutableStateOf(false) }
    var draftSuccess by remember { mutableStateOf(false) }
    val isToday = remember(uiState.permitDateMillis) {
        uiState.permitDateMillis?.let { millis ->
            val selectedDate = Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.currentSystemDefault()).date
            val today = kotlin.time.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            selectedDate == today
        } ?: false
    }

    fun parseTime(timeStr: String): Int {
        if (timeStr.isBlank()) return 0
        val parts = timeStr.trim().split(Regex("[:\\s]+"))
        if (parts.size >= 3) {
            val h = parts[0].toIntOrNull() ?: 0
            val m = parts[1].toIntOrNull() ?: 0
            val ampm = parts[2].uppercase()
            var hour24 = h
            if (ampm == "PM" && h < 12) hour24 += 12
            if (ampm == "AM" && h == 12) hour24 = 0
            return hour24 * 60 + m
        }
        return 0
    }

    LaunchedEffect(permitTypeId, isFromDraft, draftId) {
        if (isFromDraft && draftId != -1L) {
            val draft = viewModel.getDraftById(draftId)
            if (draft != null) {
                viewModel.restoreDraftData(draft)
                viewModel.fetchPermitContents(draft.permitTypeId.toInt())
            }
        } else if (permitTypeId != -1) {
            viewModel.fetchPermitContents(permitTypeId)
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .statusBarsPadding()
                    .padding(vertical = 10.dp)
                    .padding(end = 22.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavigationBackIcon(onClick = {
                    showExitPopup.value = true
                })
                Text(
                    text = stringResource(Res.string.createPermit).uppercase(),
                    style = textStyle(
                        size = 14.sp,
                        weight = FontWeight.Bold
                    )
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
                    org.example.project.utilites.AppBorderButton(
                        title = stringResource(Res.string.saveAsDraft),
                        onClick = {
                            viewModel.saveLocalDraft {
                                draftSuccess = true
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                    org.example.project.utilites.AppPrimaryButton(
                        title = stringResource(Res.string.save),
                        onClick = {
                            viewModel.submitPermit()
                        },
                        modifier = Modifier.weight(1f),
                        isLoading = uiState.isSubmitting,
                        enabled = !uiState.isSubmitting,
                        fillMaxWidth = false
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues)
                .padding(horizontal = 22.dp)
        ) {
            if (uiState.isLoading) {
                AppLoader()
            } else {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 30.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (permitTypeName.isNotBlank()) {
                        Text(
                            text = permitTypeName.uppercase(),
                            style = textStyle(size = 14.sp, weight = FontWeight.Bold),
                            color = AppColors.Primary
                        )
                    }
                    AppTextField(
                        value = "InstaResolv Private Limited",
                        onValueChange = {},
                        title = stringResource(Res.string.contractorName),
                        placeholder = "",
                        enabled = false,
                        isMandatory = true
                    )
                    AppProjectDropdown(
                        title = stringResource(Res.string.specifyProject),
                        isMandatory = true,
                        placeholder = stringResource(Res.string.chooseProject),
                        selectedProject = uiState.selectedProject,
                        onProjectSelected = { viewModel.updateSelectedProject(it) },
                        projects = uiState.projects,
                    )
                    HorizontalDivider()
                    Text(
                        text = stringResource(Res.string.permitValidity).uppercase(),
                        style = textStyle(14.sp, FontWeight.Bold),
                        color = AppColors.Primary
                    )
                    Column(
                        horizontalAlignment = Alignment.Start
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                text = stringResource(Res.string.permitDate),
                                style = textStyle(
                                    size = 12.sp,
                                    weight = FontWeight.SemiBold
                                )
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "*",
                                style = textStyle(
                                    size = 12.sp,
                                    weight = FontWeight.SemiBold
                                ),
                                color = Color.Red
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        AppDatePicker(
                            text = stringResource(Res.string.permitDate),
                            onDateSelected = { viewModel.updatePermitDate(it) },
                            selectedDateMillis = uiState.permitDateMillis,
                            restrictPastDates = true
                        )
                    }
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
                            Box {
                                AppTimePicker(
                                    text = stringResource(Res.string.txt_0000),
                                    selectedTime = uiState.startTime,
                                    onTimeSelected = { viewModel.updateStartTime(it) },
                                    enabled = uiState.permitDateMillis != null,
                                    restrictPastTime = isToday
                                )
                                if (uiState.permitDateMillis == null) {
                                    Box(
                                        modifier = Modifier
                                            .matchParentSize()
                                            .clickable { localError = "Select Date" }
                                    )
                                }
                            }
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
                            Box {
                                AppTimePicker(
                                    text = stringResource(Res.string.txt_0000),
                                    selectedTime = uiState.endTime,
                                    onTimeSelected = { endTime -> 
                                        if (uiState.startTime.isNotBlank() && parseTime(endTime) <= parseTime(uiState.startTime)) {
                                            localError = "End time must be greater than start time"
                                        } else {
                                            viewModel.updateEndTime(endTime)
                                        }
                                    },
                                    enabled = uiState.permitDateMillis != null && uiState.startTime.isNotBlank()
                                )
                                if (uiState.permitDateMillis == null) {
                                    Box(
                                        modifier = Modifier
                                            .matchParentSize()
                                            .clickable { localError = "Select Date" }
                                    )
                                } else if (uiState.startTime.isBlank()) {
                                    Box(
                                        modifier = Modifier
                                            .matchParentSize()
                                            .clickable { localError = "Select start time" }
                                    )
                                }
                            }
                        }
                    }
                    if (uiState.selectedProject != null) {
                        AppUserDropdown(
                            title = stringResource(Res.string.authorizedPerson),
                            selectedUser = uiState.selectedUser,
                            onUserSelected = { viewModel.updateSelectedUser(it) },
                            placeholder = stringResource(Res.string.chooseUser),
                            users = uiState.authorizedUsers
                        )
                    }
                    if (uiState.certificateValidity.isNotEmpty()) {
                        uiState.certificateValidity.forEach { item ->
                            AppTextField(
                                value = uiState.certificateValidityAnswers[item.id] ?: "",
                                onValueChange = {
                                    viewModel.updateCertificateValidity(
                                        item.id,
                                        it
                                    )
                                },
                                title = item.title ?: "",
                                placeholder = "Enter ${item.title ?: ""}"
                            )
                        }
                    }

                    if (uiState.generalConditions.isNotEmpty()) {
                        HorizontalDivider()
                        Text(
                            text = stringResource(Res.string.generalConditions).uppercase(),
                            style = textStyle(14.sp, FontWeight.Bold),
                            color = AppColors.Primary
                        )
                        uiState.generalConditions.forEach { item ->
                            QuestionRow(
                                title = item.title ?: "",
                                selectedAnswer = uiState.generalConditionAnswers[item.id],
                                onAnswerSelected = { answer ->
                                    viewModel.updateGeneralCondition(item.id, answer)
                                },
                                remarks = uiState.generalConditionRemarks[item.id] ?: "",
                                onRemarksChanged = { remark ->
                                    viewModel.updateGeneralConditionRemark(item.id, remark)
                                }
                            )
                        }
                    }
                    HorizontalDivider()
                    Text(
                        text = stringResource(Res.string.permitRequest).uppercase(),
                        style = textStyle(14.sp, FontWeight.Bold),
                        color = AppColors.Primary
                    )
                    AppTextField(
                        value = viewModel.logginedUser?.name ?: "",
                        onValueChange = {

                        },
                        title = stringResource(Res.string.reportedBy),
                        placeholder = stringResource(Res.string.enterReportedBy),
                        enabled = false,
                        isMandatory = true
                    )
                    AppSignCreateBox(
                        signatureUrl = uiState.signatureUrl,
                        onSignatureUploaded = { viewModel.updateSignatureUrl(it) },
                        onRemoveSignatureClick = { viewModel.updateSignatureUrl(null) },
                    )

                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = buildAnnotatedString {
                                        append("Time")
                                    },
                                    style = textStyle(12.sp, FontWeight.SemiBold)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                AppTimePicker(
                                    text = stringResource(Res.string.txt_0000),
                                    selectedTime = uiState.signatureTime,
                                    onTimeSelected = { },
                                    enabled = false
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = buildAnnotatedString {
                                        append("Date")
                                    },
                                    style = textStyle(12.sp, FontWeight.SemiBold)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                AppDatePicker(
                                    text = stringResource(Res.string.yyyymmdd),
                                    onDateSelected = { },
                                    selectedDateMillis = uiState.signatureDateMillis,
                                    enabled = false
                                )
                            }
                        }
                }

                ToastHost(
                    visible = localError != null,
                    message = localError.orEmpty(),
                    onDismiss = { localError = null },
                    type = ToastType.Error
                )
                ToastHost(
                    visible = uiState.error != null,
                    message = uiState.error.orEmpty(),
                    onDismiss = { viewModel.clearError() },
                    type = ToastType.Error
                )
                ToastHost(
                    visible = uiState.submitError != null,
                    message = uiState.submitError.orEmpty(),
                    onDismiss = { viewModel.clearError() },
                    type = ToastType.Error
                )
            }
            if (uiState.submitSuccess) {
                org.example.project.ui.components.AppStatusDialog(
                    visible = uiState.submitSuccess,
                    title = stringResource(Res.string.success),
                    description = uiState.successMessage ?: "Success",
                    buttonText = "OK",
                    onDismiss = {
                        uiState.submitSuccess = false
                        onBackClicked()
                    }
                )
            }

            if (draftSuccess) {
                org.example.project.ui.components.AppStatusDialog(
                    visible = draftSuccess,
                    title = stringResource(Res.string.success),
                    description = "Permit Saved as Draft Successfully",
                    buttonText = "OK",
                    onDismiss = {
                        draftSuccess = false
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
        }
    }
}


@Composable
@Preview
fun CreatePermitScreenPreview(){
    CreatePermitScreen()
}