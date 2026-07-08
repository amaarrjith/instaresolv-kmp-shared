package org.example.project.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
                    AppProjectDropdown(
                        title = "Specify Project",
                        placeholder = "Choose Project",
                        selectedProject = null,
                        onProjectSelected = { },
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
                            onDateSelected = { },
                            selectedDateMillis = null
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
                                selectedTime = "",
                                onTimeSelected = {  }
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
                                selectedTime = "",
                                onTimeSelected = {}
                            )
                        }
                    }
                    AppUserDropdown(
                        title = "Authorized Person",
                        selectedUser = null,
                        onUserSelected = { },
                        placeholder = "Choose User",
                        users = emptyList()
                    )
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
                        value = "",
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
                                selectedTime = "",
                                onTimeSelected = {  }
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
                                selectedDateMillis = null
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
@Preview
fun CreatePermitScreenPreview(){
    CreatePermitScreen()
}