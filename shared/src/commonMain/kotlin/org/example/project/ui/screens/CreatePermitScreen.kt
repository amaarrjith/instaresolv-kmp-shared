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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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

@Composable
fun CreatePermitScreen(
    onBackClicked: () -> Unit = {},
    permitTypeId: Int = -1,
    permitTypeName: String = ""
) {
    val viewModel: CreatePermitViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(permitTypeId) {
        if (permitTypeId != -1) {
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
                NavigationBackIcon(
                    onClick = {
                        onBackClicked()
                    }
                )
                Text(
                    text = "Create Permit".uppercase(),
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
                        title = "Save as Draft",
                        onClick = {

                        },
                        modifier = Modifier.weight(1f)
                    )
                    org.example.project.utilites.AppPrimaryButton(
                        title = "Save",
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
                        title = "Contractor Name",
                        placeholder = "",
                        enabled = false,
                        isMandatory = true
                    )
                    AppProjectDropdown(
                        title = "Specify Project",
                        placeholder = "Choose Project",
                        selectedProject = uiState.selectedProject,
                        onProjectSelected = { viewModel.updateSelectedProject(it) },
                        projects = uiState.projects,
                    )
                    HorizontalDivider()
                    Text(
                        text = "Permit Validity".uppercase(),
                        style = textStyle(14.sp, FontWeight.Bold),
                        color = AppColors.Primary
                    )
                    Column(
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Permit Date",
                            style = textStyle(
                                size = 12.sp,
                                weight = FontWeight.SemiBold
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        AppDatePicker(
                            text = "Permit Date",
                            onDateSelected = { viewModel.updatePermitDate(it) },
                            selectedDateMillis = uiState.permitDateMillis
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
                            AppTimePicker(
                                text = "00 : 00",
                                selectedTime = uiState.startTime,
                                onTimeSelected = { viewModel.updateStartTime(it) }
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
                                onTimeSelected = { viewModel.updateEndTime(it) }
                            )
                        }
                    }
                    if (uiState.selectedProject != null) {
                        AppUserDropdown(
                            title = "Authorized Person",
                            selectedUser = uiState.selectedUser,
                            onUserSelected = { viewModel.updateSelectedUser(it) },
                            placeholder = "Choose User",
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
                            text = "General Conditions".uppercase(),
                            style = textStyle(14.sp, FontWeight.Bold),
                            color = AppColors.Primary
                        )
                        uiState.generalConditions.forEach { item ->
                            QuestionRow(
                                title = item.title ?: "",
                                selectedAnswer = uiState.generalConditionAnswers[item.id],
                                onAnswerSelected = { answer ->
                                    viewModel.updateGeneralCondition(item.id, answer)
                                }
                            )
                        }
                    }
                    HorizontalDivider()
                    Text(
                        text = "Permit Request".uppercase(),
                        style = textStyle(14.sp, FontWeight.Bold),
                        color = AppColors.Primary
                    )
                    AppTextField(
                        value = viewModel.logginedUser?.name ?: "",
                        onValueChange = {

                        },
                        title = "Reported By",
                        placeholder = "Enter Reported By",
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
                                    text = "00 : 00",
                                    selectedTime = uiState.signatureTime,
                                    onTimeSelected = {  },
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
                                    text = "YYYY-MM-DD",
                                    onDateSelected = { },
                                    selectedDateMillis = uiState.signatureDateMillis,
                                    enabled = false
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        if (uiState.submitError != null) {
                            Text(
                                text = uiState.submitError ?: "",
                                color = Color.Red,
                                style = textStyle(12.sp, FontWeight.Medium),
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        if (uiState.submitSuccess) {
                            Text(
                                text = "Permit validity submitted successfully!",
                                color = Color(0xFF4CAF50),
                                style = textStyle(14.sp, FontWeight.Medium),
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                }

                ToastHost(
                    visible = uiState.error != null,
                    message = uiState.error.orEmpty(),
                    onDismiss = { viewModel.clearError() },
                    type = ToastType.Error
                )
            }
            if (uiState.submitSuccess) {
                org.example.project.ui.components.AppStatusDialog(
                    visible = uiState.submitSuccess,
                    title = "Success",
                    description = uiState.successMessage ?: "Success",
                    buttonText = "OK",
                    onDismiss = {
                        uiState.submitSuccess = false
                        onBackClicked()
                    }
                )
            }
        }
    }
}


@Composable
@Preview
fun CreatePermitScreenPreview(){
    CreatePermitScreen()
}