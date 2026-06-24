package org.example.project.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_edit
import androidx.compose.runtime.LaunchedEffect

import instaresolv.shared.generated.resources.ic_pencil
import org.example.project.colors.AppColors
import org.example.project.localization.LocalAppStrings
import org.example.project.profile.ProfileUiState
import org.example.project.profile.ProfileViewModel
import org.example.project.typography.textStyle
import org.example.project.ui.components.AppLoader
import org.example.project.ui.components.WebImageView
import org.example.project.ui.components.imagepicker.AppImagePicker
import org.example.project.utilites.AppBorderButton
import org.example.project.utilites.AppPrimaryButton
import org.example.project.utilites.AppTextField
import org.example.project.utilites.NavigationBackIcon
import org.example.project.utilites.ToastHost
import org.example.project.utilites.ToastType
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onBack: () -> Unit
) {
    val viewModel: ProfileViewModel = koinInject()
    val uiState = viewModel.uiState.collectAsState()
    Scaffold(
        topBar = {
            ProfileScreenTopBar(
                uiState = uiState,
                onEditClick = { viewModel.setEditMode(true) },
                onBackClick = {
                    if (uiState.value is ProfileUiState.isEditing) {
                        viewModel.setEditMode(false)
                    } else {
                        onBack()
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(horizontal = 28.dp)
                .imePadding()
        ) {
            ToastHost(
                visible = uiState.value is ProfileUiState.Success,
                message = (uiState.value as? ProfileUiState.Success)?.successMessage.orEmpty(),
                onDismiss = {
                    viewModel.updateUi()
                },
                type = ToastType.Success
            )
            ToastHost(
                visible = uiState.value is ProfileUiState.Error,
                message = (uiState.value as? ProfileUiState.Error)?.message.orEmpty(),
                onDismiss = {
                    viewModel.updateUi()
                },
                type = ToastType.Error
            )
            Column(
                modifier = Modifier.fillMaxSize()

            ) {
                when(uiState.value) {
                    ProfileUiState.Loading -> {
                        AppLoader()
                    }
                    ProfileUiState.isEditing -> {
                        ProfileEditScreen(
                            viewModel = viewModel
                        )
                    }
                    else -> {
                        ProfileScreenContent(
                            viewModel = viewModel,
                            onLogout = onLogout
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileScreenContent(
    viewModel: ProfileViewModel,
    onLogout: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        WebImageView(
            imageUrl = viewModel.user?.profileImage,
            modifier = Modifier
                .clip(CircleShape)
                .size(80.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = viewModel.user?.name ?: "",
            style = textStyle(
                size = 16.sp,
                weight = FontWeight.Bold
            ),
            color = AppColors.Black,
            textAlign = TextAlign.Center
        )
        Text(
            text = "EMP - ${viewModel.user?.userId.toString()}",
            style = textStyle(
                size = 12.sp,
                weight = FontWeight.Bold
            ),
            color = AppColors.TextGray
        )
    }
    Spacer(Modifier.height(46.dp))
    val strings = LocalAppStrings.current
    ProfileScreenItem(strings.fullName, viewModel.user?.name ?: "")
    ProfileScreenItem(strings.emailId, viewModel.user?.email ?: "")
    ProfileScreenItem(strings.designation, viewModel.user?.designation ?: "")
    ProfileScreenItem(strings.company, viewModel.user?.company ?: "")
    Spacer(modifier = Modifier.height(60.dp))
    AppBorderButton(
        title = strings.logout,
        onClick = {
            viewModel.logout()
            onLogout()
        },
    )
}

@Composable
fun ProfileEditScreen(
    viewModel: ProfileViewModel
) {
    var name by remember { mutableStateOf(viewModel.user?.name ?: "") }
    var email by remember { mutableStateOf(viewModel.user?.email ?: "") }
    var designation by remember { mutableStateOf(viewModel.user?.designation ?: "") }
    var company by remember { mutableStateOf(viewModel.user?.company ?: "") }
    var profileImage by remember { mutableStateOf(viewModel.user?.profileImage ?: "") }
    
    val showImagePicker = remember { mutableStateOf(false) }
    val isImageUploading = remember { mutableStateOf(false) }
    val strings = LocalAppStrings.current

    LaunchedEffect(viewModel) {}

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppImagePicker(
                showPicker = showImagePicker,
                showFullScreenLoader = false,
                onIsUploading = { isImageUploading.value = it },
                onImageUploaded = {
                    profileImage = it
                }
            )

            Box(
                modifier = Modifier.size(80.dp),
                contentAlignment = Alignment.Center
            ) {
                WebImageView(
                    imageUrl = profileImage,
                    modifier = Modifier.fillMaxSize().clip(CircleShape)
                )

                if (isImageUploading.value) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }

                if (profileImage.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(AppColors.Primary)
                            .clickable {
                                showImagePicker.value = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painterResource(Res.drawable.ic_pencil),
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
            if (profileImage.isEmpty()) {
                AppBorderButton(
                    title = strings.uploadProfile,
                    onClick = {
                        showImagePicker.value = true
                    }
                )
            }
        }
    }
    Spacer(Modifier.height(46.dp))
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        AppTextField(
            value = name,
            onValueChange = { name = it },
            title = strings.fullName,
            placeholder = strings.fullName
        )
        AppTextField(
            value = email,
            onValueChange = { email = it },
            title = strings.emailId,
            placeholder = strings.emailId,
            enabled = false
        )
        AppTextField(
            value = designation,
            onValueChange = { designation = it },
            title = strings.designation,
            placeholder = strings.designation
        )
        AppTextField(
            value = company,
            onValueChange = { company = it },
            title = strings.company,
            placeholder = strings.company
        )
        AppPrimaryButton(
            title = strings.save,
            onClick = {
                viewModel.saveProfile(name, profileImage, email, designation, company)
            }
        )
    }
}

@Composable
fun ProfileScreenTopBar(
    uiState: State<ProfileUiState>,
    onEditClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    val strings = LocalAppStrings.current
    Row(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .padding(bottom = 20.dp)
        ,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavigationBackIcon(
            onClick = onBackClick
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = strings.profile.uppercase(),
            style = textStyle(
                size = 14.sp,
                weight = FontWeight.Bold
            ),
            color = AppColors.Black
        )
        Spacer(modifier = Modifier.weight(1f))
        if (uiState.value !is ProfileUiState.isEditing) {
            Row(
                modifier = Modifier
                    .padding(end = 26.dp)
            ) {
                Image(
                    painter = painterResource(Res.drawable.ic_edit),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Edit".uppercase(),
                    modifier = Modifier.clickable {
                        onEditClick()
                    },
                    style = textStyle(
                        size = 13.sp,
                        weight = FontWeight.SemiBold
                    ),
                    color = AppColors.SkyBlue
                )
            }
        }
    }
}

@Composable
fun ProfileScreenItem(
    title: String,
    subText: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = title,
            style = textStyle(
                size = 12.sp,
                weight = FontWeight.SemiBold
            ),
            color = AppColors.Black
        )
        Text(
            text = subText,
            style = textStyle(
                size = 12.sp,
                weight = FontWeight.Medium
            ),
            color = AppColors.TextGray,
        )
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 13.dp)
        )
    }
}


@Composable
@Preview
fun ProfileScreenPreview() {
    ProfileScreen(
        onLogout = {},
        onBack = {}
    )
}