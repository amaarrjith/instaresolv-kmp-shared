package org.example.project.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.assignedToMe
import instaresolv.shared.generated.resources.assignedTrainings
import instaresolv.shared.generated.resources.changeDesignation
import instaresolv.shared.generated.resources.changeRole
import instaresolv.shared.generated.resources.ic_add_training
import instaresolv.shared.generated.resources.ic_completed
import instaresolv.shared.generated.resources.ic_email
import instaresolv.shared.generated.resources.ic_not_completed
import instaresolv.shared.generated.resources.ic_search
import instaresolv.shared.generated.resources.notifications
import instaresolv.shared.generated.resources.ic_play
import instaresolv.shared.generated.resources.ic_checkbox_on
import instaresolv.shared.generated.resources.ic_checkbox_off
import org.example.project.colors.AppColors
import org.example.project.data.model.AllTrainingData
import org.example.project.data.model.DesignationTypeResponse
import org.example.project.data.model.ProjectMember
import org.example.project.data.model.UserRole
import org.example.project.typography.textStyle
import org.example.project.ui.components.WebImageView
import org.example.project.ui.components.AppSuccessDialog
import org.example.project.utilites.ToastHost
import org.example.project.utilites.ToastType
import org.example.project.utilites.NavigationBackIcon
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.example.project.data.model.AssignedTrainingData
import org.example.project.ui.components.AppLoader
import org.example.project.utilites.ErrorRetryView
import org.example.project.ui.screens.EmptyScreenView
import org.example.project.utilites.AppPrimaryButton
import org.example.project.utilites.AppSearchBar
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignedTrainingsView(
    member: ProjectMember,
    onBackClick: () -> Unit,
    viewModel: AssignedTrainingsViewModel = koinInject()
) {
    val designationTypes = viewModel.designations.collectAsState().value
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(member.userId) {
        viewModel.initUserId(member.userId)
    }

    var isSearchVisible by remember { mutableStateOf(false) }

    // Toast/Dialog state handling
    LaunchedEffect(uiState.assignSuccessMessage, uiState.assignErrorMessage) {
        if (!uiState.assignErrorMessage.isNullOrBlank()) {
            // Wait for toast if needed
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color.White,
            topBar = {
                Row(
                    modifier = Modifier
                        .statusBarsPadding()
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NavigationBackIcon(
                        onClick = onBackClick
                    )
                    Text(
                        text = stringResource(Res.string.assignedTrainings).uppercase(),
                        style = textStyle(
                            size = 14.sp,
                            weight = FontWeight.Bold
                        ),
                        color = AppColors.Black
                    )
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Box(modifier = Modifier.padding(horizontal = 22.dp)) {
                    MemberDetailsView(
                        member = member,
                        designationTypes = designationTypes
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp)
                        .padding(vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(Res.string.assignedTrainings),
                        style = textStyle(
                            size = 14.sp,
                            weight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Image(
                        painter = painterResource(Res.drawable.ic_search),
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            isSearchVisible = !isSearchVisible
                        }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Image(
                        painter = painterResource(Res.drawable.ic_add_training),
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            viewModel.setAddTrainingModalVisible(true)
                        }
                    )
                }

                if (isSearchVisible) {
                    AppSearchBar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 22.dp)
                            .padding(bottom = 10.dp),
                        value = uiState.searchQuery,
                        onValueChange = { viewModel.updateSearchQuery(it) }
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .padding(horizontal = 22.dp)
                ) {
                    if (uiState.isLoading && uiState.trainings.isEmpty()) {
                        AppLoader()
                    } else if (uiState.error != null && uiState.trainings.isEmpty()) {
                        ErrorRetryView(
                            errorMessage = uiState.error ?: "",
                            onRetryClick = { viewModel.loadTrainings(isRefresh = true) }
                        )
                    } else {
                        PullToRefreshBox(
                            isRefreshing = uiState.isLoading,
                            onRefresh = { viewModel.loadTrainings(isRefresh = true) },
                            modifier = Modifier.fillMaxSize()
                        ) {
                            if (uiState.trainings.isEmpty()) {
                                EmptyScreenView(
                                    message = "No trainings found."
                                )
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                    contentPadding = PaddingValues(
                                        top = 10.dp,
                                        bottom = 24.dp
                                    )
                                ) {
                                    items(uiState.trainings.size) { index ->
                                        if (index >= uiState.trainings.size - 1 && !uiState.isLoading && !uiState.isPaginating && !uiState.isLastPage) {
                                            LaunchedEffect(key1 = index) {
                                                viewModel.loadTrainings(isRefresh = false)
                                            }
                                        }
                                        AssignedTrainingListItem(
                                            training = uiState.trainings[index],
                                            onClick = { }
                                        )
                                        if (index < uiState.trainings.size - 1) {
                                            Spacer(modifier = Modifier.height(16.dp))
                                            HorizontalDivider(color = Color(0xFFF2F2F2))
                                        }
                                    }
                                    if (uiState.isPaginating) {
                                        item {
                                            Box(
                                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(24.dp),
                                                    color = AppColors.Primary
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Toasts and Dialogs overlay
            if (!uiState.assignErrorMessage.isNullOrBlank()) {
                ToastHost(
                    message = uiState.assignErrorMessage ?: "",
                    type = ToastType.Error,
                    onDismiss = { viewModel.clearMessages() },
                    visible = !uiState.assignErrorMessage.isNullOrBlank(),
                )
            }

            AppSuccessDialog(
                visible = !uiState.assignSuccessMessage.isNullOrBlank(),
                title = "Success",
                description = uiState.assignSuccessMessage ?: "",
                onDismiss = { viewModel.clearMessages() }
            )

            AddTrainingModal(
                isVisible = uiState.isAddTrainingModalVisible,
                trainings = uiState.allTrainings,
                selectedIds = uiState.selectedTrainingIds,
                isLoading = uiState.isLoadingAllTrainings,
                isPaginating = uiState.isPaginatingAllTrainings,
                isLastPage = uiState.isLastPageAllTrainings,
                isAssigning = uiState.isAssigning,
                onToggleSelection = { viewModel.toggleTrainingSelection(it) },
                onSubmit = { viewModel.assignSelectedTrainings() },
                onDismiss = { viewModel.setAddTrainingModalVisible(false) },
                onLoadMore = { viewModel.loadAllTrainings(false) }
            )
        }
    }
}

@Composable
fun MemberDetailsView(
    member: ProjectMember,
    designationTypes: List<DesignationTypeResponse>,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(75.dp)
                    .align(Alignment.CenterVertically),
                contentAlignment = Alignment.BottomCenter
            ) {
                WebImageView(
                    imageUrl = member.image,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .size(50.dp)
                        .clip(CircleShape)
                )
                MemberStatusIcon(UserRole.fromInt(member.role) == UserRole.ADMIN)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = member.name,
                    style = textStyle(
                        size = 14.sp,
                        weight = FontWeight.Bold
                    )
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(Res.drawable.ic_email),
                        contentDescription = null
                    )
                    Text(
                        text = member.email,
                        style = textStyle(
                            size = 12.sp,
                            weight = FontWeight.Normal
                        ),
                        color = AppColors.TextGray
                    )
                }
                val designationTitles = member.designation.mapNotNull { id ->
                    designationTypes.find { it.id == id }?.designation
                }.joinToString(", ")

                if (designationTitles.isNotEmpty()) {
                    Text(
                        text = designationTitles,
                        style = textStyle(
                            size = 12.sp,
                            weight = FontWeight.Normal
                        ),
                        color = AppColors.BlackText
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Composable
fun AssignedTrainingListItem(
    training: AssignedTrainingData,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Thumbnail Image with Play overlay
        Box(
            modifier = Modifier
                .size(width = 80.dp, height = 75.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            val thumbnail = training.thumbnailImage
            if (thumbnail.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFEEEEEE)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.White, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "?",
                            style = textStyle(size = 18.sp, weight = FontWeight.Bold, color = Color.LightGray)
                        )
                    }
                }
            } else {
                WebImageView(
                    imageUrl = thumbnail,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Play icon overlay
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(Res.drawable.ic_play),
                    contentDescription = "Play",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Details column
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Status and Code Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val (badgeText, badgeBg, badgeTextColor) = when (training.status) {
                    0 -> Triple("NOT STARTED", Color(0xFF2E6AC6), Color.White)
                    1 -> Triple("IN PROGRESS", Color(0xFFF57C00), Color.White)
                    2 -> Triple("FINISHED TRAINING", Color(0xFF00A82B), Color.White)
                    3 -> Triple("PASSED", Color(0xFF00A82B), Color.White)
                    else -> Triple("COMPLETED", Color(0xFF00A82B), Color.White)
                }

                Box(
                    modifier = Modifier
                        .background(badgeBg, shape = RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badgeText,
                        style = textStyle(size = 9.sp, weight = FontWeight.Bold, color = badgeTextColor),
                        letterSpacing = 0.2.sp
                    )
                }

                if (!training.trainingCode.isNullOrBlank()) {
                    Text(
                        text = training.trainingCode,
                        style = textStyle(size = 11.sp, weight = FontWeight.SemiBold, color = AppColors.DarkGray)
                    )
                }
            }

            // Title
            Text(
                text = training.title,
                style = textStyle(size = 14.sp, weight = FontWeight.Bold, color = AppColors.Black),
                maxLines = 2,
                lineHeight = 18.sp
            )

            // Progress bar (only for In Progress / status = 1)
            if (training.status == 1) {
                val progressInt = training.progressInt
                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "$progressInt%",
                        style = textStyle(size = 10.sp, weight = FontWeight.Medium, color = AppColors.DarkGray),
                        modifier = Modifier.align(Alignment.End)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .background(Color(0xFFE5E5E5), shape = RoundedCornerShape(2.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progressInt.toFloat() / 100f)
                                .height(4.dp)
                                .background(Color(0xFFD32F2F), shape = RoundedCornerShape(2.dp)) // Red progress bar
                        )
                    }
                }
            } else if (training.status == 0) {
                 Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "0%",
                        style = textStyle(size = 10.sp, weight = FontWeight.Medium, color = AppColors.DarkGray),
                        modifier = Modifier.align(Alignment.End)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .background(Color(0xFFE5E5E5), shape = RoundedCornerShape(2.dp))
                    )
                }
            }

            // Chips spacing
            Spacer(modifier = Modifier.height(2.dp))

            // Checkboxes Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusCheckItem("Video", training.isVideoPlayed)
                StatusCheckItem("Quiz", training.isQuizAttended)
                StatusCheckItem("Certificate", training.isCertificateAvailable)
            }
        }
    }
}

@Composable
fun StatusCheckItem(label: String, isCompleted: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if(isCompleted) {
            Image(
                painter = painterResource(Res.drawable.ic_completed),
                contentDescription = null
            )
        } else {
            Image(
                painter = painterResource(Res.drawable.ic_not_completed),
                contentDescription = null
            )
        }
        Text(
            text = label,
            style = textStyle(size = 10.sp, weight = FontWeight.Medium, color = AppColors.DarkGray)
        )
    }
}

@Composable
fun AddTrainingModal(
    isVisible: Boolean,
    trainings: List<AllTrainingData>,
    selectedIds: Set<Int>,
    isLoading: Boolean,
    isPaginating: Boolean,
    isLastPage: Boolean,
    isAssigning: Boolean,
    onToggleSelection: (Int) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit,
    onLoadMore: () -> Unit
) {
    if (!isVisible) return

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(12.dp))
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Select Training Video",
                    style = textStyle(size = 16.sp, weight = FontWeight.Bold, color = AppColors.Black),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = AppColors.Primary)
                    }
                } else if (trainings.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        Text("No trainings available", style = textStyle(size = 14.sp, color = AppColors.DarkGray))
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().height(300.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(trainings.size) { index ->
                            val training = trainings[index]
                            
                            // Trigger load more when approaching the end
                            if (index >= trainings.size - 3) {
                                LaunchedEffect(key1 = index) {
                                    onLoadMore()
                                }
                            }
                            
                            val isSelected = selectedIds.contains(training.id)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onToggleSelection(training.id) },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Thumbnail Image
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                ) {
                                    if (training.thumbnailImage.isNullOrBlank()) {
                                        Box(
                                            modifier = Modifier.fillMaxSize().background(Color(0xFFEEEEEE)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("?", style = textStyle(size = 14.sp, weight = FontWeight.Bold, color = Color.LightGray))
                                        }
                                    } else {
                                        WebImageView(
                                            imageUrl = training.thumbnailImage,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                    
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Image(
                                            painter = painterResource(Res.drawable.ic_play),
                                            contentDescription = "Play",
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                // Details
                                Column(modifier = Modifier.weight(1f)) {
                                    if (!training.trainingCode.isNullOrBlank()) {
                                        Text(
                                            text = training.trainingCode,
                                            style = textStyle(size = 10.sp, weight = FontWeight.Medium, color = AppColors.DarkGray)
                                        )
                                    }
                                    Text(
                                        text = training.title,
                                        style = textStyle(size = 13.sp, weight = FontWeight.SemiBold, color = AppColors.BlackText),
                                        maxLines = 2,
                                        lineHeight = 16.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                // Checkbox
                                Image(
                                    painter = painterResource(if (isSelected) Res.drawable.ic_checkbox_on else Res.drawable.ic_checkbox_off),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        
                        if (isPaginating) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = AppColors.Primary
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppPrimaryButton(
                        title = "Submit",
                        isLoading = isAssigning,
                        onClick = onSubmit,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

