package org.example.project.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_mark_location
import kotlinx.datetime.LocalDate
import org.example.project.colors.AppColors
import org.example.project.typography.textStyle
import org.example.project.ui.components.AppProjectDropdown
import org.example.project.ui.components.AppUserDropdown
import org.example.project.ui.components.AppImageCreateBox
import org.example.project.ui.components.AppDatePicker
import org.example.project.utilites.AppTextField
import org.example.project.utilites.NavigationBackIcon
import org.koin.compose.koinInject
import org.example.project.utilites.ToastHost
import kotlinx.datetime.toLocalDateTime
import org.example.project.utilites.ToastType
import org.example.project.ui.components.AppMultilineTextField
import org.example.project.utilites.AppBorderButton
import org.example.project.utilites.AppPrimaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateViolationScreen(
    onBackClicked: () -> Unit
) {
    val viewModel: CreateViolationViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = androidx.compose.ui.platform.LocalFocusManager.current
    val showSuccessDialog = remember { mutableStateOf(false) }

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
            Row (
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(vertical = 10.dp)
                    .padding(end = 22.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavigationBackIcon(onBackClicked)
                Text(
                    text = "Create - Violation".uppercase(),
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
                            viewModel.saveViolation(
                                isDraft = true,
                                onSuccess = { showSuccessDialog.value = true }
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                    AppPrimaryButton(
                        title = "Publish",
                        onClick = {
                            viewModel.saveViolation(
                                isDraft = false,
                                onSuccess = { showSuccessDialog.value = true }
                            )
                        },
                        modifier = Modifier.weight(1f),
                        isLoading = uiState.isLoading,
                        enabled = !uiState.isLoading,
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
                // Facility / Project
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AppProjectDropdown(
                        onProjectSelected = { it?.let { viewModel.updateSelectedProject(it) } },
                        selectedProject = uiState.selectedProject
                    )
                }

                // Employee Name
                AppTextField(
                    value = viewModel.user?.name ?: "",
                    onValueChange = {  },
                    title = "Reported By",
                    placeholder = "",
                    isMandatory = true,
                    readOnly = true
                )

                // Employee Name
                AppTextField(
                    value = uiState.employeeName,
                    onValueChange = { viewModel.updateEmployeeName(it) },
                    title = "Employee Name",
                    placeholder = "Employee Name",
                    isMandatory = true,
                )

                // Employee ID
                AppTextField(
                    value = uiState.employeeId,
                    onValueChange = { viewModel.updateEmployeeId(it) },
                    title = "Employee ID",
                    placeholder = "EMP-XXXXX",
                    isMandatory = true,
                )

                // Violation Date
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            text = "Violation Date",
                            style = textStyle(size = 12.sp, weight = FontWeight.SemiBold),
                            color = AppColors.Black
                        )
                        Text(
                            text = "*",
                            style = textStyle(size = 12.sp, weight = FontWeight.SemiBold),
                            color = Color.Red
                        )
                    }
                    // We just need a simple DatePicker, assuming AppDatePicker is correct.
                    // Converting YYYY-MM-DD to DD-MM-YYYY in viewmodel or passing it properly
                    // The design says "18 Aug 2025" and the API wants "dd-MM-yyyy"
                    AppDatePicker(
                        text = "DD-MM-YYYY",
                        selectedDateMillis = uiState.violationDateMillis,
                        onDateSelected = { timeMillis ->
                            if (timeMillis != null) {
                                val instant = kotlinx.datetime.Instant.fromEpochMilliseconds(timeMillis)
                                val timeZone = kotlinx.datetime.TimeZone.currentSystemDefault()
                                val localDate = instant.toLocalDateTime(timeZone).date
                                val dateStr = "${localDate.day.toString().padStart(2, '0')}-${localDate.monthNumber.toString().padStart(2, '0')}-${localDate.year}"
                                viewModel.updateViolationDate(dateStr, timeMillis)
                            }
                        }
                    )
                    // If AppDatePicker returns a formatted string directly or how is it used in CreateIncident? Let me verify AppDatePicker later, for now we will just use string value in the TextField if AppDatePicker doesn't fit exactly.
                }

                // Location
                AppTextField(
                    icon = Res.drawable.ic_mark_location,
                    value = uiState.location,
                    onValueChange = { viewModel.updateLocation(it) },
                    title = "Location",
                    placeholder = "Enter Location"
                )

                // Description
                AppMultilineTextField(
                    value = uiState.description,
                    onValueChange = { viewModel.updateDescription(it) },
                    title = "Description",
                    placeholder = "Enter here"
                )

                // Upload Images
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Upload Image",
                            style = textStyle(size = 12.sp, weight = FontWeight.SemiBold),
                            color = AppColors.Black
                        )
                    }

                    uiState.images.forEachIndexed { index, imageState ->
                        AppImageCreateBox(
                            imageUrl = imageState.imageUrl,
                            description = imageState.description,
                            onDescriptionChange = { viewModel.updateImageDescription(imageState.id, it) },
                            onImageUploaded = { viewModel.updateImageBlock(imageState.id, it) },
                            onRemoveImageClick = { viewModel.removeImageBlock(imageState.id) }
                        )
                    }

                    Text(
                        text = "+ Add Image",
                        style = textStyle(size = 14.sp, weight = FontWeight.SemiBold),
                        color = AppColors.Primary,
                        modifier = Modifier
                            .clickable { viewModel.addImageBlock() }
                            .padding(vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            ToastHost(
                visible = uiState.error != null,
                message = uiState.error.orEmpty(),
                onDismiss = { viewModel.clearError() },
                type = ToastType.Error,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        if (showSuccessDialog.value) {
            org.example.project.ui.components.AppStatusDialog(
                visible = showSuccessDialog.value,
                title = "Success",
                description = "Violation created successfully.",
                buttonText = "OK",
                onDismiss = {
                    showSuccessDialog.value = false
                    onBackClicked()
                }
            )
        }
    }
}
