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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_add
import instaresolv.shared.generated.resources.ic_mark_location
import org.example.project.colors.AppColors
import org.example.project.typography.textStyle
import org.example.project.ui.components.AppProjectDropdown
import org.example.project.ui.components.AppDatePicker
import org.example.project.utilites.AppTextField
import org.example.project.utilites.NavigationBackIcon
import org.example.project.utilites.AppBorderButton
import org.example.project.utilites.AppPrimaryButton
import org.example.project.ui.components.AppMultilineTextField
import org.example.project.ui.components.AppImageCreateBox
import org.example.project.utilites.ToastHost
import org.example.project.utilites.ToastType
import org.example.project.ui.components.AppStatusDialog
import org.koin.compose.koinInject
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateInspectionScreen(
    inspectionTypeId: Int,
    inspectionTypeName: String,
    onBackClicked: () -> Unit
) {
    val viewModel: CreateInspectionViewModel = koinInject()
    
    LaunchedEffect(inspectionTypeId, inspectionTypeName) {
        viewModel.init(inspectionTypeId, inspectionTypeName)
    }
    
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
                    text = "Create - Audit & Inspections".uppercase(),
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
                            viewModel.saveInspection(isDraft = true, onSuccess = { showSuccessDialog.value = true })
                        },
                        modifier = Modifier.weight(1f)
                    )
                    AppPrimaryButton(
                        title = "Save",
                        onClick = {
                            viewModel.saveInspection(isDraft = false, onSuccess = { showSuccessDialog.value = true })
                        },
                        modifier = Modifier.weight(1f),
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
                // Inspection Type Header
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Inspection Type",
                        style = textStyle(size = 12.sp, weight = FontWeight.SemiBold),
                        color = Color.Gray
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = uiState.inspectionTypeName.ifEmpty { "Static Equipment Inspection" },
                            style = textStyle(size = 14.sp, weight = FontWeight.Bold),
                            color = AppColors.Black
                        )
                    }
                }

                // Facility / Project
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AppProjectDropdown(
                        onProjectSelected = { viewModel.onProjectSelected(it) },
                        selectedProject = uiState.selectedProject
                    )
                }

                // Equipment Description
                AppTextField(
                    value = uiState.equipmentDescription,
                    onValueChange = { viewModel.onEquipmentDescriptionChanged(it) },
                    title = "Model Number",
                    placeholder = "Enter Model Number",
                    isMandatory = true,
                )

                // Inspected By
                AppTextField(
                    value = viewModel.user?.name ?: "",
                    onValueChange = {},
                    title = "Inspected By",
                    placeholder = "",
                    isMandatory = true,
                    enabled = false
                )

                // Location
                AppTextField(
                    icon = Res.drawable.ic_mark_location,
                    value = uiState.location,
                    onValueChange = { viewModel.onLocationChanged(it) },
                    title = "Location",
                    placeholder = "Downtown Dubai, UAE",
                    isMandatory = true
                )

                // Date
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            text = "Date",
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
                        text = "18 Aug 2025",
                        selectedDateMillis = uiState.inspectionDateMillis,
                        onDateSelected = { viewModel.onDateSelected(it) }
                    )
                }

                // Equipment Source (Radio)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            text = "Equipment Source",
                            style = textStyle(size = 12.sp, weight = FontWeight.SemiBold),
                            color = AppColors.Black
                        )
                        Text(
                            text = "*",
                            style = textStyle(size = 12.sp, weight = FontWeight.SemiBold),
                            color = Color.Red
                        )
                    }
                    val sources = listOf("ALNASR", "SUBCONTRACTOR", "RENTAL")

                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        sources.chunked(2).forEach { rowItems ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowItems.forEach { source ->
                                    Row(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                viewModel.onEquipmentSourceChanged(source)
                                            },
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier.size(24.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            RadioButton(
                                                selected = uiState.equipmentSource == source,
                                                onClick = { viewModel.onEquipmentSourceChanged(source) },
                                                colors = RadioButtonDefaults.colors(
                                                    selectedColor = AppColors.Primary
                                                )
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(4.dp))

                                        Text(
                                            text = source,
                                            style = textStyle(size = 13.sp, weight = FontWeight.Medium),
                                            color = AppColors.Black,
                                            maxLines = 1
                                        )
                                    }
                                }

                                // Fill remaining space if last row has only one item
                                if (rowItems.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }

                // Subcontractor Text Field (Conditionally visible)
                if (uiState.equipmentSource == "SUBCONTRACTOR") {
                    AppTextField(
                        value = uiState.equipmentSourceSecondary,
                        onValueChange = { viewModel.onEquipmentSourceSecondaryChanged(it) },
                        title = "Subcontractor Name",
                        placeholder = "Enter Subcontractor Name",
                        isMandatory = false
                    )
                }

                if (uiState.isFetchingQuestions) {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = AppColors.Primary, modifier = Modifier.size(30.dp))
                    }
                } else if (uiState.questions.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        uiState.questions.forEach { question ->
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = question.title,
                                    style = textStyle(size = 12.sp, weight = FontWeight.SemiBold),
                                    color = AppColors.Black
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    val options = listOf("Yes", "No", "Not Applicable")
                                    options.forEach { option ->
                                        Row(
                                            modifier = Modifier.clickable { viewModel.onAnswerChanged(question.id, option) },
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier.size(24.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                RadioButton(
                                                    selected = uiState.answers[question.id] == option,
                                                    onClick = { viewModel.onAnswerChanged(question.id, option) },
                                                    colors = RadioButtonDefaults.colors(selectedColor = AppColors.Primary)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (option == "Not Applicable") "NA" else option.uppercase(),
                                                style = textStyle(size = 13.sp, weight = FontWeight.Medium),
                                                color = AppColors.Black,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Warning Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFF3EE), RoundedCornerShape(4.dp))
                            .padding(16.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "No Questions Found !",
                                style = textStyle(size = 14.sp, weight = FontWeight.Bold),
                                color = AppColors.Primary
                            )
                            Text(
                                text = "You can't publish or save as a draft\nwithout adding at least one question.",
                                style = textStyle(size = 12.sp, weight = FontWeight.Medium),
                                color = AppColors.Primary
                            )
                        }
                    }
                }

                // Description
                AppMultilineTextField(
                    value = uiState.description,
                    onValueChange = { viewModel.onDescriptionChanged(it) },
                    title = "Description",
                    placeholder = "Enter Description",
                )

                // Notes
                AppMultilineTextField(
                    value = uiState.notes,
                    onValueChange = { viewModel.onNotesChanged(it) },
                    title = "Notes",
                    placeholder = "Enter Notes",
                )

                // Image slots
                uiState.inspectionImages.forEachIndexed { index, inspectionImage ->
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
                            imageUrl = inspectionImage.imageUrl,
                            description = inspectionImage.description,
                            onDescriptionChange = { viewModel.onImageDescriptionChange(index, it) },
                            onImageUploaded = { viewModel.onImageSelected(index, it) },
                            onRemoveImageClick = { viewModel.onImageRemoved(index) }
                        )
                    }
                }

                if (uiState.inspectionImages.size < 6) {
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
                                weight = FontWeight.Bold
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

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                        .clickable(enabled = false) {},
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AppColors.Primary)
                }
            }
        }

        if (showSuccessDialog.value) {
            AppStatusDialog(
                visible = showSuccessDialog.value,
                title = "Success",
                description = "Inspection created successfully.",
                buttonText = "OK",
                onDismiss = {
                    showSuccessDialog.value = false
                    onBackClicked()
                }
            )
        }
    }
}
