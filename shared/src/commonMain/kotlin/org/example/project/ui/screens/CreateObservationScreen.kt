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
import org.example.project.data.model.ObservationGroup
import org.example.project.typography.textStyle
import org.example.project.ui.components.AppLoader
import org.example.project.ui.components.AppMultilineTextField
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
import org.jetbrains.compose.resources.stringResource
import instaresolv.shared.generated.resources.*
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateObservationScreen(
    isFromDraft: Boolean = false,
    draftId: Long = -1L,
    onBackClicked: () -> Unit
) {
    val viewModel: CreateObservationViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()
    val observationTitle = remember { mutableStateOf("") }
    val location = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
    val focusManager = androidx.compose.ui.platform.LocalFocusManager.current
    val showSuccessDialog = remember { mutableStateOf(false) }

    LaunchedEffect(isFromDraft, draftId) {
        if (isFromDraft && draftId != -1L) {
            val draft = viewModel.getDraftById(draftId)
            if (draft != null) {
                observationTitle.value = draft.title
                location.value = draft.location
                description.value = draft.description
                
                viewModel.restoreDraftData(draft)
            }
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
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(vertical = 10.dp)
                    .padding(end = 22.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavigationBackIcon(onBackClicked)
                Text(
                    text = stringResource(Res.string.createObservation),
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
                        title = stringResource(Res.string.saveAsDraft),
                        onClick = {
                            viewModel.saveObservation(
                                draftId = if (isFromDraft && draftId != -1L) draftId else null,
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
                        title = stringResource(Res.string.save),
                        onClick = {
                            viewModel.saveObservation(
                                draftId = if (isFromDraft && draftId != -1L) draftId else null,
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
                    title = stringResource(Res.string.title),
                    placeholder = stringResource(Res.string.enterObservationTitle)
                )
                AppTextField(
                    isMandatory = true,
                    value = viewModel.logginedUser?.name ?: "",
                    onValueChange = { },
                    title = stringResource(Res.string.reportedBy),
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
                        title = stringResource(Res.string.responsiblePerson),
                        placeholder = stringResource(Res.string.enterResponsiblePersonName)
                    )
                    AppTextField(
                        value = uiState.manualResponsibleEmail,
                        onValueChange = { viewModel.onManualResponsibleEmailChange(it) },
                        title = stringResource(Res.string.responsiblePersonEmail),
                        placeholder = stringResource(Res.string.enterResponsiblePersonEmail)
                    )
                } else {
                    AppUserDropdown(
                        title = stringResource(Res.string.responsiblePerson),
                        placeholder = stringResource(Res.string.selectResponsiblePerson),
                        users = uiState.groupUsers,
                        selectedUser = uiState.selectedResponsiblePerson,
                        onUserSelected = { viewModel.onResponsiblePersonSelected(it) }
                    )
                    AppUserDropdown(
                        title = stringResource(Res.string.sendNotificationTo),
                        placeholder = stringResource(Res.string.selectPersonToNotify),
                        users = uiState.groupUsers,
                        selectedUser = uiState.selectedNotifyPerson,
                        onUserSelected = { viewModel.onNotifyPersonSelected(it) }
                    )
                }
                
                AppTextField(
                    icon = Res.drawable.ic_mark_location,
                    value = location.value, // TODO: Use separate location state
                    onValueChange = { location.value = it },
                    title = stringResource(Res.string.location),
                    placeholder = stringResource(Res.string.enterLocation)
                )
                AppMultilineTextField(
                    value = description.value,
                    onValueChange = { description.value = it },
                    title = stringResource(Res.string.description1),
                    placeholder = stringResource(Res.string.enterDescription),
                    isVoiceEnabled = true,
                    onAudioUrlProcessed = { viewModel.onAudioUrlProcessed(it) }
                )
                
                uiState.observationImages.forEachIndexed { index, observationImage ->
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = stringResource(Res.string.uploadImage_key, index + 1),
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
                            },
                            isAIDescriptionEnabled = true
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
                            contentDescription = stringResource(Res.string.addImage),
                            modifier = Modifier.size(15.dp),
                            tint = org.example.project.colors.AppColors.Primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(Res.string.addImage),
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
                title = stringResource(Res.string.success),
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
