package org.example.project.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import instaresolv.shared.generated.resources.ic_add
import org.example.project.colors.AppColors
import org.example.project.data.model.GroupUser
import org.example.project.data.model.Project
import org.example.project.typography.textStyle
import org.example.project.ui.components.AppProjectDropdown
import org.example.project.ui.components.AppUserDropdown
import org.example.project.ui.components.AppImageCreateBox
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import org.example.project.utilites.AppTextField
import org.example.project.utilites.NavigationBackIcon
import org.koin.compose.koinInject
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import org.example.project.utilites.ToastHost
import org.example.project.utilites.ToastType
import androidx.compose.foundation.clickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateObservationScreen(
    onBackClicked: () -> Unit
) {
    val viewModel: CreateObservationViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()
    val observationTitle = remember { mutableStateOf("") }
    val location = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
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
                    text = "CREATE - OBSERVATION",
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
                    org.example.project.utilites.AppBorderButton(
                        title = "Save as Draft",
                        onClick = {
                            viewModel.saveObservation(
                                title = observationTitle.value,
                                location = location.value,
                                description = description.value,
                                isDraft = true,
                                onSuccess = { showSuccessDialog.value = true }
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                    org.example.project.utilites.AppPrimaryButton(
                        title = "Save",
                        onClick = {
                            viewModel.saveObservation(
                                title = observationTitle.value,
                                location = location.value,
                                description = description.value,
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
                AppTextField(
                    isMandatory = true,
                    value = observationTitle.value,
                    onValueChange = { observationTitle.value = it },
                    title = "Title",
                    placeholder = "Enter Observation Title"
                )
                AppTextField(
                    isMandatory = true,
                    value = viewModel.logginedUser?.name ?: "",
                    onValueChange = { },
                    title = "Reported By",
                    placeholder = "",
                    readOnly = true,
                    enabled = false
                )
                AppProjectDropdown(
                    onProjectSelected = {
                        viewModel.onProjectSelected(it)
                    },
                    selectedProject = uiState.selectedProject
                )
                
                if (uiState.selectedProject == null) {
                    AppTextField(
                        value = uiState.manualResponsibleName,
                        onValueChange = { viewModel.onManualResponsibleNameChange(it) },
                        title = "Responsible Person",
                        placeholder = "Enter Responsible Person Name"
                    )
                    AppTextField(
                        value = uiState.manualResponsibleEmail,
                        onValueChange = { viewModel.onManualResponsibleEmailChange(it) },
                        title = "Responsible Person Email",
                        placeholder = "Enter Responsible Person Email"
                    )
                } else {
                    AppUserDropdown(
                        title = "Responsible Person",
                        placeholder = "Select Responsible Person",
                        users = uiState.groupUsers,
                        selectedUser = uiState.selectedResponsiblePerson,
                        onUserSelected = { viewModel.onResponsiblePersonSelected(it) }
                    )
                    AppUserDropdown(
                        title = "Send Notification To",
                        placeholder = "Select Person To Notify",
                        users = uiState.groupUsers,
                        selectedUser = uiState.selectedNotifyPerson,
                        onUserSelected = { viewModel.onNotifyPersonSelected(it) }
                    )
                }
                
                AppTextField(
                    icon = Res.drawable.ic_mark_location,
                    value = location.value, // TODO: Use separate location state
                    onValueChange = { location.value = it },
                    title = "Location",
                    placeholder = "Enter Location"
                )
                AppTextField(
                    value = description.value,
                    onValueChange = { description.value = it },
                    title = "Description",
                    placeholder = "Enter Description",
                    singleLine = false,
                    textFieldModifier = Modifier.fillMaxWidth().height(120.dp)
                )
                
                uiState.observationImages.forEachIndexed { index, observationImage ->
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Upload Image ${index + 1}",
                            style = textStyle(
                                size = 12.sp,
                                weight = FontWeight.SemiBold
                            ),
                            color = AppColors.Black
                        )
                        AppImageCreateBox(
                            imageUrl = observationImage.imageUrl,
                            description = observationImage.description,
                            onDescriptionChange = { viewModel.onImageDescriptionChange(index, it) },
                            onImageUploaded = {
                                viewModel.onImageSelected(index, it)
                            },
                            onRemoveImageClick = {
                                viewModel.onImageRemoved(index)
                            }
                        )
                    }
                }
                if (uiState.observationImages.size < 6) {
                    androidx.compose.material3.TextButton(
                        onClick = { viewModel.onAddImageSlot() },
                        modifier = Modifier.align(Alignment.Start)
                    ) {
                        androidx.compose.material3.Icon(
                            painter = org.jetbrains.compose.resources.painterResource(Res.drawable.ic_add),
                            contentDescription = "Add Image",
                            modifier = Modifier.size(15.dp),
                            tint = org.example.project.colors.AppColors.Primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Add Image",
                            style = org.example.project.typography.textStyle(
                                size = 12.sp,
                                weight = FontWeight.SemiBold
                            ),
                            color = org.example.project.colors.AppColors.Primary
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
                description = "Observation created successfully.",
                buttonText = "OK",
                onDismiss = {
                    showSuccessDialog.value = false
                    onBackClicked()
                }
            )
        }
    }
}
