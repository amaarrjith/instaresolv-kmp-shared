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
import instaresolv.shared.generated.resources.ic_add
import org.example.project.colors.AppColors
import org.example.project.data.model.LessonLearnedImageRequest
import org.example.project.typography.textStyle
import org.example.project.ui.components.AppMultilineTextField
import org.example.project.ui.components.AppProjectDropdown
import org.example.project.ui.components.AppImageCreateBox
import org.example.project.utilites.AppTextField
import org.example.project.utilites.NavigationBackIcon
import org.example.project.utilites.ToastHost
import org.example.project.utilites.ToastType
import org.koin.compose.koinInject
import org.jetbrains.compose.resources.stringResource
import instaresolv.shared.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateLessonsLearnedScreen(
    onBackClicked: () -> Unit
) {
    val viewModel: CreateLessonLearnedViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = androidx.compose.ui.platform.LocalFocusManager.current
    val showSuccessDialog = remember { mutableStateOf(false) }

    var selectedProject by remember { mutableStateOf<org.example.project.data.model.Project?>(null) }
    var title by remember { mutableStateOf("") }
    var reportedBy by remember { mutableStateOf(viewModel.user?.name ?: "") }
    var description by remember { mutableStateOf("") }

    val images = remember { mutableStateListOf(ObservationImage()) }

    var showErrorToast by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState) {
        when (uiState) {
            is CreateLessonLearnedUiState.Success -> {
                viewModel.resetState()
                showSuccessDialog.value = true
            }
            is CreateLessonLearnedUiState.Error -> {
                showErrorToast = (uiState as CreateLessonLearnedUiState.Error).message
                viewModel.resetState()
            }
            else -> {}
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
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(vertical = 10.dp)
                    .padding(end = 22.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavigationBackIcon(onBackClicked)
                Text(
                    text = stringResource(Res.string.createLessonLearned),
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
                        },
                        modifier = Modifier.weight(1f)
                    )
                    org.example.project.utilites.AppPrimaryButton(
                        title = stringResource(Res.string.save),
                        onClick = {
                            val imageRequests = images.filter { it.imageUrl?.isNotBlank() == true }.map {
                                LessonLearnedImageRequest(
                                    image = it.imageUrl ?: "",
                                    description = it.description,
                                    isAiGeneratedDescription = false
                                )
                            }
                            viewModel.createLessonLearned(
                                title = title,
                                description = description,
                                reportedBy = reportedBy,
                                images = imageRequests,
                                facilitiesId = selectedProject?.groupId ?: -1
                            )
                        },
                        modifier = Modifier.weight(1f),
                        isLoading = uiState is CreateLessonLearnedUiState.Loading,
                        enabled = uiState !is CreateLessonLearnedUiState.Loading,
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
                AppProjectDropdown(
                    onProjectSelected = { project ->
                        selectedProject = project
                    },
                    selectedProject = selectedProject,
                )

                AppTextField(
                    isMandatory = true,
                    value = title,
                    onValueChange = { title = it },
                    title = stringResource(Res.string.title),
                    placeholder = stringResource(Res.string.enterLessonLearnedTitle)
                )

                AppTextField(
                    isMandatory = true,
                    value = reportedBy,
                    onValueChange = {  },
                    title = stringResource(Res.string.reportedBy),
                    placeholder = stringResource(Res.string.enterReportedBy),
                    enabled = false
                )

                AppMultilineTextField(
                    value = description,
                    onValueChange = { description = it },
                    title = stringResource(Res.string.description),
                    placeholder = stringResource(Res.string.enterDescription),
                )

                images.forEachIndexed { index, observationImage ->
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
                            onDescriptionChange = { newDesc ->
                                images[index] = observationImage.copy(description = newDesc)
                            },
                            onImageUploaded = { newUrl ->
                                images[index] = observationImage.copy(imageUrl = newUrl)
                            },
                            onRemoveImageClick = {
                                if (images.size > 1) {
                                    images.removeAt(index)
                                } else {
                                    images[index] = ObservationImage()
                                }
                            }
                        )
                    }
                }
                if (images.size < 6) {
                    TextButton(
                        onClick = {
                            if (images.size < 6) {
                                images.add(ObservationImage())
                            }
                        },
                        modifier = Modifier.align(Alignment.Start)
                    ) {
                        Icon(
                            painter = org.jetbrains.compose.resources.painterResource(Res.drawable.ic_add),
                            contentDescription = stringResource(Res.string.addImage),
                            modifier = Modifier.size(15.dp),
                            tint = AppColors.Primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(Res.string.addImage),
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
                visible = showErrorToast != null,
                message = showErrorToast.orEmpty(),
                onDismiss = { showErrorToast = null },
                type = ToastType.Error
            )
        }

        if (showSuccessDialog.value) {
            org.example.project.ui.components.AppStatusDialog(
                visible = showSuccessDialog.value,
                title = stringResource(Res.string.success),
                description = "Lesson Learned created successfully.",
                buttonText = "OK",
                onDismiss = {
                    showSuccessDialog.value = false
                    onBackClicked()
                }
            )
        }
    }
}
