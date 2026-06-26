package org.example.project.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_add
import instaresolv.shared.generated.resources.ic_camera
import instaresolv.shared.generated.resources.ic_right_icon
import instaresolv.shared.generated.resources.ic_share
import instaresolv.shared.generated.resources.ic_translate

import org.example.project.colors.AppColors
import org.example.project.data.model.ObservationDetailResponse
import org.example.project.data.model.Project
import org.example.project.data.settings.formatDate
import org.example.project.typography.textStyle
import org.example.project.ui.ObservationStatus
import org.example.project.ui.ProjectListCard
import org.example.project.ui.components.AppImageCreateBox
import org.example.project.ui.components.AppLoader
import org.example.project.ui.components.AppMultilineTextField
import org.example.project.ui.components.WebImageView
import org.example.project.utilites.AppBorderButton
import org.example.project.utilites.ErrorRetryView
import org.example.project.utilites.ToastHost
import org.example.project.utilites.ToastType
import org.example.project.ui.components.AppStatusDialog
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

@Composable
fun ObservationDetailScreen(
    observationId: Int,
    onRefreshList: () -> Unit,
    onBackClicked: () -> Unit
) {
    val viewModel: ObservationDetailViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()
    val closeUiState by viewModel.closeUiState.collectAsState()
    
    var isClosingObservation by remember { mutableStateOf(false) }
    var closeDescription by remember { mutableStateOf("") }
    val closeImages = remember { androidx.compose.runtime.mutableStateListOf(ObservationImage()) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(observationId) {
        viewModel.loadObservationDetail(observationId)
    }

    LaunchedEffect(closeUiState) {
        when (val state = closeUiState) {
            is CloseObservationState.Success -> {
                showSuccessDialog = true
                viewModel.resetCloseState()
            }
            is CloseObservationState.Error -> {
                errorMessage = state.message
                viewModel.resetCloseState()
            }
            else -> {}
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().imePadding(),
        containerColor = Color.Transparent, // To match BottomSheet dimming behind it if any
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (uiState is ObservationDetailUiState.Success) {
                Surface(color = Color.White, shadowElevation = 8.dp) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 22.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (isClosingObservation) {
                            AppBorderButton(
                                title = "Back",
                                onClick = { isClosingObservation = false },
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(Modifier.width(16.dp))
                            AppBorderButton(
                                title = "Close",
                                onClick = {
                                    if (closeDescription.isBlank()) {
                                        errorMessage = "Description is mandatory"
                                    } else {
                                        viewModel.closeObservation(observationId, closeDescription, closeImages)
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            AppBorderButton(
                                title = "Generate PDF",
                                onClick = {

                                },
                                modifier = Modifier.weight(1f)
                            )
                            Row(
                                modifier = Modifier.weight(1f)
                                    .clickable { }
                                    .height(48.dp)
                                    .padding(horizontal = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Share", style = textStyle(size = 14.sp, weight = FontWeight.Bold), color = AppColors.Black)
                                Spacer(Modifier.width(8.dp))
                                Image(
                                    painter = painterResource(Res.drawable.ic_share),
                                    contentDescription = null,
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Bottom bar padding
                .padding(top = 40.dp) // Offset from top to look like a sheet
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(Color.White)
        ) {
            when (val state = uiState) {
                is ObservationDetailUiState.Idle, is ObservationDetailUiState.Loading -> {
                    AppLoader()
                }
                is ObservationDetailUiState.Error -> {
                    ErrorRetryView(
                        errorMessage = state.message,
                        onRetryClick = {
                            viewModel.loadObservationDetail(observationId)
                        }
                    )
                }
                is ObservationDetailUiState.Success -> {
                    ObservationDetailContent(
                        detail = state.detail,
                        isClosingObservation = isClosingObservation,
                        onCloseObservationClick = { isClosingObservation = true },
                        closeDescription = closeDescription,
                        onDescriptionChange = { closeDescription = it },
                        closeImages = closeImages
                    )
                }
            }

            ToastHost(
                visible = errorMessage != null,
                message = errorMessage ?: "",
                onDismiss = { errorMessage = null },
                type = ToastType.Error,
                modifier = Modifier.padding(horizontal = 22.dp)
            )
        }
        
        if (showSuccessDialog) {
            AppStatusDialog(
                visible = true,
                title = "Success",
                description = "Observation closed successfully.",
                buttonText = "OK",
                onDismiss = {
                    onRefreshList()
                    showSuccessDialog = false
                    onBackClicked()
                }
            )
        }
    }
}

@Composable
fun ObservationDetailContent(
    detail: ObservationDetailResponse,
    isClosingObservation: Boolean,
    onCloseObservationClick: () -> Unit,
    closeDescription: String,
    onDescriptionChange: (String) -> Unit,
    closeImages: androidx.compose.runtime.snapshots.SnapshotStateList<ObservationImage>
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 22.dp, vertical = 16.dp),
    ) {
        // Drag handle
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(4.dp)
                .clip(CircleShape)
                .background(Color(0xFFE5E5EA))
                .align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
                Text(
                    text = "Title",
                    style = textStyle(size = 12.sp, weight = FontWeight.Medium),
                    color = AppColors.TextGray
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = detail.observationTitle ?: "Untitled",
                    style = textStyle(size = 16.sp, weight = FontWeight.Bold),
                    color = AppColors.Black
                )
            }
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFF8F9098)), // Gray background for translate icon
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(Res.drawable.ic_translate),
                    contentDescription = null,
                )
            }
        }
        Spacer(Modifier.height(24.dp))

        Text(
            text = "Project",
            style = textStyle(size = 12.sp, weight = FontWeight.Medium),
            color = AppColors.TextGray
        )
        Spacer(Modifier.height(11.dp))
        Row {
            WebImageView(
                imageUrl = detail.group?.groupImage,
                modifier = Modifier.size(42.dp)
                    .clip(RoundedCornerShape(15))
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = detail.group?.groupName ?: "",
                    style = textStyle(size = 13.sp, weight = FontWeight.SemiBold),
                    color = AppColors.Black
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Gray)
                ) {
                    Text(
                        text = detail.group?.groupCode.toString(),
                        modifier = Modifier.padding(vertical = 2.dp, horizontal = 5.dp),
                        style = textStyle(
                            size = 10.sp,
                            weight = FontWeight.SemiBold
                        ),
                        color = Color.White
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        HorizontalDivider(color = Color(0xFFF0F0F5))
        Spacer(Modifier.height(24.dp))

        AnimatedContent(
            targetState = isClosingObservation,
            transitionSpec = {
                if (targetState) {
                    slideInHorizontally { width -> width } + fadeIn() togetherWith
                            slideOutHorizontally { width -> -width } + fadeOut()
                } else {
                    slideInHorizontally { width -> -width } + fadeIn() togetherWith
                            slideOutHorizontally { width -> width } + fadeOut()
                }
            }
        ) { isClosing ->
            if (isClosing) {
                CloseObservationForm(
                    description = closeDescription,
                    onDescriptionChange = onDescriptionChange,
                    observationImages = closeImages
                )
            } else {
                ObservationDetailInfo(detail, onCloseObservationClick)
            }
        }
    }
}

@Composable
fun ObservationDetailInfo(
    detail: ObservationDetailResponse,
    onCloseObservationClick: () -> Unit
) {
    Column {
        Text(
            text = "Reported By",
            style = textStyle(size = 12.sp, weight = FontWeight.Medium),
            color = AppColors.TextGray
        )
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            WebImageView(
                imageUrl = "", // Using responsiblePerson image as fallback for mock UI
                modifier = Modifier.size(25.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = detail.reportedBy ?: "",
                style = textStyle(size = 14.sp, weight = FontWeight.SemiBold),
                color = AppColors.Black
            )
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Responsible Person",
            style = textStyle(size = 12.sp, weight = FontWeight.Medium),
            color = AppColors.TextGray
        )
        Spacer(Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            WebImageView(
                imageUrl = detail.responsiblePerson?.image,
                modifier = Modifier.size(25.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = detail.responsiblePerson?.name ?: "",
                style = textStyle(size = 14.sp, weight = FontWeight.SemiBold),
                color = AppColors.Black
            )
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Responsible Person Email",
            style = textStyle(size = 12.sp, weight = FontWeight.Medium),
            color = AppColors.TextGray
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = detail.responsiblePerson?.email ?: "",
            style = textStyle(size = 14.sp, weight = FontWeight.SemiBold),
            color = AppColors.Black
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Location",
            style = textStyle(size = 12.sp, weight = FontWeight.Medium),
            color = AppColors.TextGray
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = detail.location ?: "",
            style = textStyle(size = 14.sp, weight = FontWeight.SemiBold),
            color = AppColors.Black
        )

        Spacer(Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Date",
                    style = textStyle(size = 12.sp, weight = FontWeight.Medium),
                    color = AppColors.TextGray
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = formatDate(detail.date ?: "", inputPattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", outputPattern = "dd MMM yyyy"),
                    style = textStyle(size = 14.sp, weight = FontWeight.SemiBold),
                    color = AppColors.Black
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Status",
                    style = textStyle(size = 12.sp, weight = FontWeight.Medium),
                    color = AppColors.TextGray
                )
                Spacer(Modifier.height(4.dp))
                val status = ObservationStatus.fromId(detail.status ?: -1)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background((status.backgroundColor))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = status.name.uppercase(),
                        style = textStyle(size = 10.sp, weight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Description (AI Translated)",
            style = textStyle(size = 12.sp, weight = FontWeight.Medium),
            color = AppColors.TextGray
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = detail.description ?: "",
            style = textStyle(size = 14.sp, weight = FontWeight.Medium),
            color = AppColors.Black
        )

        Spacer(Modifier.height(24.dp))
        HorizontalDivider(color = Color(0xFFF0F0F5))
        Spacer(Modifier.height(24.dp))

        Text(
            text = "Uploaded Images",
            style = textStyle(size = 12.sp, weight = FontWeight.Medium),
            color = AppColors.TextGray
        )
        Spacer(Modifier.height(12.dp))

        if (!detail.imageDescription.isNullOrEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                detail.imageDescription.forEach { img ->
                    Column {
                        if (!img.image.isNullOrEmpty()) {
                            WebImageView(
                                imageUrl = img.image,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                        if (!img.description.isNullOrEmpty()) {
                            Text(
                                text = img.description,
                                style = textStyle(size = 14.sp, weight = FontWeight.Normal),
                                color = AppColors.Black
                            )
                        }
                    }
                }
            }
        } else {
            EmptyScreenView(
                message = "No Images Found"
            )
        }

        Spacer(Modifier.height(24.dp))
        HorizontalDivider(color = Color(0xFFF0F0F5))
        Spacer(Modifier.height(24.dp))
        if (ObservationStatus.fromId(detail.status ?: -1) == ObservationStatus.OPEN) {
            Text(
                text = "Actions",
                style = textStyle(size = 14.sp, weight = FontWeight.Bold),
                color = AppColors.Black
            )
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth().clickable { onCloseObservationClick() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Close Observation",
                        style = textStyle(size = 14.sp, weight = FontWeight.Bold),
                        color = AppColors.Primary
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Close this observation with detailed",
                        style = textStyle(size = 12.sp, weight = FontWeight.Normal),
                        color = AppColors.TextGray
                    )
                }
                Image(
                    painter = painterResource(Res.drawable.ic_right_icon),
                    contentDescription = null
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun CloseObservationForm(
    description: String,
    onDescriptionChange: (String) -> Unit,
    observationImages: androidx.compose.runtime.snapshots.SnapshotStateList<ObservationImage>
) {
    Column {
        Text(
            text = "Close Observation",
            style = textStyle(size = 16.sp, weight = FontWeight.Bold),
            color = AppColors.Black
        )
        Spacer(Modifier.height(16.dp))
        
        AppMultilineTextField(
            value = description,
            onValueChange = onDescriptionChange,
            title = "Description",
            placeholder = "Enter description"
        )
        
        Spacer(Modifier.height(24.dp))
        
        observationImages.forEachIndexed { index, observationImage ->
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
                org.example.project.ui.components.AppImageCreateBox(
                    imageUrl = observationImage.imageUrl,
                    description = observationImage.description,
                    onDescriptionChange = { newDesc ->
                        observationImages[index] = observationImage.copy(description = newDesc)
                    },
                    onImageUploaded = { newUrl ->
                        observationImages[index] = observationImage.copy(imageUrl = newUrl)
                    },
                    onRemoveImageClick = {
                        observationImages.removeAt(index)
                    }
                )
            }
            Spacer(Modifier.height(16.dp))
        }
        
        if (observationImages.size < 6) {
            androidx.compose.material3.TextButton(
                onClick = { observationImages.add(ObservationImage()) },
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
}
