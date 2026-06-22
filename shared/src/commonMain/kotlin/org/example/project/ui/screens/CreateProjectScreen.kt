package org.example.project.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_sucess_logo
import org.example.project.colors.AppColors
import org.example.project.data.model.CreateProjectResponse
import org.example.project.data.model.Project
import org.example.project.project.CreateProjectViewModel
import org.example.project.typography.textStyle
import org.example.project.ui.components.WebImageView
import org.example.project.utilites.AppBorderButton
import org.example.project.utilites.AppPrimaryButton
import org.example.project.utilites.AppTextField
import org.example.project.utilites.NavigationBackIcon
import org.example.project.project.CreateProjectUiState
import org.example.project.ui.ProjectListCard
import org.example.project.ui.components.AppLoader
import org.example.project.utilites.ToastHost
import org.example.project.utilites.ToastType
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import com.preat.peekaboo.image.picker.ResizeOptions
import androidx.compose.runtime.rememberCoroutineScope
import com.preat.peekaboo.ui.camera.PeekabooCamera
import com.preat.peekaboo.ui.camera.rememberPeekabooCameraState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.graphics.SolidColor
import org.example.project.ui.components.imagepicker.AppImagePicker
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.shape.CircleShape

@Composable
fun CreateProjectScreen(
    onBack: () -> Unit
) {
    val viewModel: CreateProjectViewModel = koinInject()
    val uiState = viewModel.uiState.collectAsState()
    val projectImage = viewModel.uploadedImageUrl.collectAsState()
    val projectTitle = remember { mutableStateOf("") }
    val projectDescription = remember { mutableStateOf("") }
    val showImagePicker = remember { mutableStateOf(false) }
    val isImageUploading = remember { mutableStateOf(false) }

    AppImagePicker(
        showPicker = showImagePicker,
        showFullScreenLoader = false,
        onIsUploading = { isImageUploading.value = it },
        onImageUploaded = { url ->
            viewModel.setUploadedImageUrl(url)
        }
    )

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CreateProjectScreenTopBar(
                onBack = {onBack()}
            )
        },
        bottomBar = {
            CreateProjectScreenBottomBar(
                onCancel = {    onBack()    },
                onCreate = {    viewModel.createProject(
                    groupCode = "",
                    groupName = projectTitle.value,
                    description = projectDescription.value,
                    groupImage = projectImage.value
                )   }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .padding(horizontal = 22.dp)
        ) {
            if (uiState.value is CreateProjectUiState.Success) {
                Dialog(
                    onDismissRequest = {
                        viewModel.clearState()
                        onBack()
                    }
                ) {
                    CreateProjectScreenContent(
                        viewModel = viewModel,
                        uiState = uiState.value as CreateProjectUiState.Success,
                        onBack = {
                            viewModel.clearState()
                            onBack()
                        }
                    )
                }
            }
            ToastHost(
                visible = uiState.value is CreateProjectUiState.Error,
                message = (uiState.value as? CreateProjectUiState.Error)?.errorMessage.orEmpty(),
                onDismiss = {
                    viewModel.clearState()
                },
                type = ToastType.Error
            )
            when(uiState.value) {
                is CreateProjectUiState.isLoading -> {
                    AppLoader()
                } else -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.height(32.dp))
                    Box(
                        modifier = Modifier.size(84.dp).clip(CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        WebImageView(
                            imageUrl = projectImage.value,
                            modifier = Modifier.fillMaxSize()
                        )
                        if (isImageUploading.value) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.4f)),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    AppBorderButton(
                        title = "Upload Project Image",
                        onClick = {
                            showImagePicker.value = true
                        },
                        modifier = Modifier.width(200.dp)
                    )
                    Spacer(modifier = Modifier.height(35.dp))
                    AppTextField(
                        value = projectTitle.value,
                        onValueChange = { projectTitle.value = it },
                        title = "Project Title",
                        placeholder = "Project Title"
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    AppTextField(
                        value = projectDescription.value,
                        onValueChange = { projectDescription.value = it },
                        title = "Project Description",
                        placeholder = "Project Description",
                        singleLine = false,
                        textFieldModifier = Modifier.fillMaxWidth().height(120.dp)
                    )
                }
            }
            }
        }
    }
}



@Composable
fun CreateProjectScreenTopBar(
    onBack: () -> Unit
) {
    Row(modifier = Modifier
        .padding(vertical = 10.dp)
        .statusBarsPadding(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavigationBackIcon(
            onClick = {
                onBack()
            }
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = "Create Project".uppercase(),
            style = textStyle(
                size = 14.sp,
                weight = FontWeight.Bold
            )
        )
    }
}

@Composable
fun CreateProjectScreenBottomBar(
    onCancel: () -> Unit,
    onCreate: () -> Unit
) {
    Row(
        modifier = Modifier
            .dropShadow(
                shape = RectangleShape,
                shadow = Shadow(
                    radius = 26.dp,
                    color = Color(0x0F000000),
                    offset = DpOffset(0.dp, (-4).dp)
                )
            )
            .background(Color.White)
            .fillMaxWidth()
            .padding(horizontal = 22.dp)
            .padding(vertical = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        AppBorderButton(
            title = "Cancel",
            onClick = {
                onCancel()
            },
            modifier = Modifier.weight(1f)
        )
        AppPrimaryButton(
            title = "Create Project",
            onClick = {
                onCreate()
            },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun CreateProjectScreenContent(
    viewModel: CreateProjectViewModel,
    uiState: CreateProjectUiState.Success,
    onBack: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painterResource(Res.drawable.ic_sucess_logo),
                contentDescription = null
            )
            Spacer(modifier = Modifier.height(25.dp))
            Text(
                text = "Project Created Successfully!",
                style = textStyle(size = 16.sp, weight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(AppColors.TextGray.copy(alpha = 0.3f))
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row() {
                    Text(
                        text = "Code Generated",
                        style = textStyle(size = 13.sp, weight = FontWeight.Medium)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "Copy Code",
                        style = textStyle(size = 13.sp, weight = FontWeight.SemiBold),
                        color = AppColors.Primary
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = uiState.project.groupCode,
                    style = textStyle(
                        size = 30.sp,
                        FontWeight.SemiBold
                    )
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    "Project Info",
                    style = textStyle(
                        13.sp,
                        FontWeight.Normal
                    )
                )
                val project = Project(
                    groupId = uiState.project.groupId,
                    groupName = uiState.project.groupName,
                    groupImage = uiState.project.groupImage,
                    groupCode = uiState.project.groupCode
                )
                ProjectListCard(project = project)
            }
            Spacer(Modifier.height(28.dp))
            AppPrimaryButton(
                title = "Close",
                onClick = {
                    viewModel.clearState()
                    onBack()
                }
            )
        }
    }

}
