package org.example.project.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.horizontalScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_mark_location
import instaresolv.shared.generated.resources.ic_mark_location
import instaresolv.shared.generated.resources.ic_add
import instaresolv.shared.generated.resources.ic_trash
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
import org.example.project.utilites.ToastType
import org.example.project.ui.IncidentType
import org.example.project.ui.components.AddEmployeeBlock
import org.example.project.ui.components.AppMultilineTextField
import org.example.project.data.model.InjuredEmployee
import org.example.project.ui.components.AppTimePicker
import org.example.project.utilites.AppBorderButton
import org.example.project.utilites.AppPrimaryButton
import org.jetbrains.compose.resources.painterResource
import org.example.project.ui.components.BulkEmployeeUploadSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateIncidentScreen(
    onBackClicked: () -> Unit
) {
    val viewModel: CreateIncidentViewModel = koinInject()
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
                    text = "Create - Incidents".uppercase(),
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
                            viewModel.saveIncident(
                                isDraft = true,
                                onSuccess = { showSuccessDialog.value = true }
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                    AppPrimaryButton(
                        title = "Publish",
                        onClick = {
                            viewModel.saveIncident(
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
                        onProjectSelected = { viewModel.onProjectSelected(it) },
                        selectedProject = uiState.selectedProject
                    )
                }

                // Reported By
                AppTextField(
                    value = uiState.reportedByName,
                    onValueChange = {},
                    title = "Reported By",
                    placeholder = "",
                    enabled = false,
                    isMandatory = true
                )


                // Incident Date
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            text = "Incident Date",
                            style = textStyle(size = 12.sp, weight = FontWeight.SemiBold),
                            color = AppColors.Black
                        )
                        Text(
                            text = "*",
                            style = textStyle(size = 12.sp, weight = FontWeight.SemiBold),
                            color = Color.Red
                        )
                    }
                    AppDatePicker(
                        text = "YYYY-MM-DD",
                        selectedDateMillis = uiState.incidentDateMillis,
                        onDateSelected = { viewModel.onDateSelected(it) }
                    )
                }

                // Incident Time
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            text = "Incident Time",
                            style = textStyle(size = 12.sp, weight = FontWeight.SemiBold),
                            color = AppColors.Black
                        )
                        Text(
                            text = "*",
                            style = textStyle(size = 12.sp, weight = FontWeight.SemiBold),
                            color = Color.Red
                        )
                    }
                    AppTimePicker(
                        text = "HH:MM",
                        selectedTime = uiState.incidentTime,
                        onTimeSelected = { viewModel.onTimeChanged(it) }
                    )
                }

                AppTextField(
                        icon = Res.drawable.ic_mark_location,
                        value = uiState.location,
                        onValueChange = { viewModel.onLocationChanged(it) },
                        title = "Location",
                        placeholder = "Enter Location"
                )


                // Incident Type
                Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            text = "Incident Type",
                            style = textStyle(size = 12.sp, weight = FontWeight.SemiBold),
                            color = AppColors.Black
                        )
                        Text(
                            text = "*",
                            style = textStyle(size = 12.sp, weight = FontWeight.SemiBold),
                            color = Color.Red
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    val incidentTypes = IncidentType.entries
                    incidentTypes.forEach { type ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.onIncidentTypeToggled(type.id) }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                                Checkbox(
                                    checked = uiState.incidentTypes.contains(type.id),
                                    onCheckedChange = { viewModel.onIncidentTypeToggled(type.id) },
                                    colors = CheckboxDefaults.colors(checkedColor = AppColors.Primary)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = type.title.uppercase(),
                                style = textStyle(size = 12.sp, weight = FontWeight.Medium),
                                color = AppColors.Black
                            )
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFFE5E5E5))

                // Injured Person Details
                Text(
                    text = "Injured Person Details",
                    style = textStyle(size = 12.sp, weight = FontWeight.SemiBold),
                    color = AppColors.Black
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            text = "Are there any injured person ?",
                            style = textStyle(size = 12.sp, weight = FontWeight.SemiBold),
                            color = AppColors.Black
                        )
                        Text(
                            text = "*",
                            style = textStyle(size = 12.sp, weight = FontWeight.SemiBold),
                            color = Color.Red
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { viewModel.onInjuredPersonChanged(true) }) {
                            Box(modifier = Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                                RadioButton(
                                    selected = uiState.hasInjuredPerson == true,
                                    onClick = { viewModel.onInjuredPersonChanged(true) },
                                    colors = RadioButtonDefaults.colors(selectedColor = AppColors.Primary)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Yes".uppercase(),
                                style = textStyle(size = 14.sp, weight = FontWeight.Medium),
                                color = AppColors.Black
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { viewModel.onInjuredPersonChanged(false) }) {
                            Box(modifier = Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                                RadioButton(
                                    selected = uiState.hasInjuredPerson == false,
                                    onClick = { viewModel.onInjuredPersonChanged(false) },
                                    colors = RadioButtonDefaults.colors(selectedColor = AppColors.Primary)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "No".uppercase(),
                                style = textStyle(size = 14.sp, weight = FontWeight.Medium),
                                color = AppColors.Black
                            )
                        }
                    }
                }

                if (uiState.hasInjuredPerson == true) {
                    if (uiState.injuredEmployees.isNotEmpty()) {
                        Spacer(Modifier.height(16.dp))
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text("Employee Details", style = textStyle(size = 12.sp, weight = FontWeight.SemiBold), color = AppColors.Black)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp)
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
                                            Text(
                                                "Employee & ID",
                                                modifier = Modifier.width(200.dp),
                                                style = textStyle(
                                                    size = 12.sp,
                                                    weight = FontWeight.Medium
                                                ),
                                                color = AppColors.TextGray
                                            )
                                            Text(
                                                "Company Name",
                                                modifier = Modifier.width(150.dp),
                                                style = textStyle(
                                                    size = 12.sp,
                                                    weight = FontWeight.Medium
                                                ),
                                                color = AppColors.TextGray
                                            )
                                            Text(
                                                "Profession",
                                                modifier = Modifier.width(150.dp),
                                                style = textStyle(
                                                    size = 12.sp,
                                                    weight = FontWeight.Medium
                                                ),
                                                color = AppColors.TextGray
                                            )
                                            Text(
                                                "Action",
                                                modifier = Modifier.width(60.dp),
                                                style = textStyle(
                                                    size = 12.sp,
                                                    weight = FontWeight.Medium
                                                ),
                                                color = AppColors.TextGray
                                            )
                                        }

                                        // Data Rows
                                        uiState.injuredEmployees.forEachIndexed { index, employee ->
                                            Row(
                                                modifier = Modifier
                                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.width(200.dp)) {
                                                    Text(
                                                        employee.employeeName ?: "",
                                                        style = textStyle(
                                                            size = 13.sp,
                                                            weight = FontWeight.SemiBold
                                                        ),
                                                        color = AppColors.Black
                                                    )
                                                    Text(
                                                        employee.employeeCode ?: "",
                                                        style = textStyle(
                                                            size = 11.sp,
                                                            weight = FontWeight.Normal
                                                        ),
                                                        color = AppColors.TextGray
                                                    )
                                                }
                                                Text(
                                                    employee.companyName ?: "",
                                                    modifier = Modifier.width(150.dp),
                                                    style = textStyle(
                                                        size = 13.sp,
                                                        weight = FontWeight.Normal
                                                    ),
                                                    color = AppColors.Black
                                                )
                                                Text(
                                                    employee.profession ?: "",
                                                    modifier = Modifier.width(150.dp),
                                                    style = textStyle(
                                                        size = 13.sp,
                                                        weight = FontWeight.Normal
                                                    ),
                                                    color = AppColors.Black
                                                )
                                                Box(
                                                    modifier = Modifier.width(60.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Image(
                                                        painter = painterResource(Res.drawable.ic_trash),
                                                        contentDescription = null,
                                                        modifier = Modifier
                                                            .size(20.dp)
                                                            .clickable { viewModel.onRemoveInjuredEmployee(index) }
                                                    )
                                                }
                                            }

                                            if (index < uiState.injuredEmployees.lastIndex) {
                                                HorizontalDivider(
                                                    color = Color(0xFFF5F5F5),
                                                    modifier = Modifier.padding(horizontal = 16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    AddEmployeeBlock(
                        onAddEmployee = { viewModel.onAddInjuredEmployee(it) },
                        onError = { err ->
                            viewModel.setError(err)
                        },
                        isProjectSelected = uiState.selectedProject != null,
                        onUploadEmployeesClick = { viewModel.openBulkUploadSheet() }
                    )
                }

                // Description
                AppMultilineTextField(
                    value = uiState.description,
                    onValueChange = { viewModel.onDescriptionChanged(it) },
                    title = "Description",
                    placeholder = "Enter Description"
                )
                AppMultilineTextField(
                    value = uiState.immediateCorrections,
                    onValueChange = { viewModel.onCorrectionsChanged(it) },
                    title = "Immediate Corrections",
                    placeholder = "Enter Immediate Corrections"
                )
                // Image slots
                uiState.incidentImages.forEachIndexed { index, incidentImage ->
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = if (index == 0) "Upload Image" else "Upload Image ${index + 1}",
                            style = textStyle(
                                size = 12.sp,
                                weight = FontWeight.SemiBold
                            ),
                            color = AppColors.Black
                        )
                        AppImageCreateBox(
                            imageUrl = incidentImage.imageUrl,
                            description = incidentImage.description,
                            onDescriptionChange = { viewModel.onImageDescriptionChange(index, it) },
                            onImageUploaded = { viewModel.onImageSelected(index, it) },
                            onRemoveImageClick = { viewModel.onImageRemoved(index) }
                        )
                    }
                }

                if (uiState.incidentImages.size < 6) {
                    TextButton(
                        onClick = { viewModel.onAddImageSlot() },
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

            ToastHost(
                visible = uiState.error != null,
                message = uiState.error.orEmpty(),
                onDismiss = { viewModel.clearError() },
                type = ToastType.Error
            )
        }

        if (showSuccessDialog.value) {
            org.example.project.ui.components.AppStatusDialog(
                visible = showSuccessDialog.value,
                title = "Success",
                description = "Incident created successfully.",
                buttonText = "OK",
                onDismiss = {
                    showSuccessDialog.value = false
                    onBackClicked()
                }
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
}
