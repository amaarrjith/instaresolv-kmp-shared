package org.example.project.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import org.example.project.colors.AppColors
import org.example.project.data.model.ObservationDetailResponse
import org.example.project.typography.textStyle
import org.example.project.ui.components.AppLoader
import org.example.project.utilites.NavigationBackIcon
import org.example.project.ui.components.WebImageView
import org.koin.compose.koinInject

@Composable
fun ObservationDetailScreen(
    observationId: Int,
    onBackClicked: () -> Unit
) {
    val viewModel: ObservationDetailViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(observationId) {
        viewModel.loadObservationDetail(observationId)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().imePadding(),
        containerColor = Color.White,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
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
                    text = "OBSERVATION DETAILS",
                    style = textStyle(
                        size = 14.sp,
                        weight = FontWeight.Bold
                    ),
                    color = AppColors.Black
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            when (val state = uiState) {
                is ObservationDetailUiState.Idle, is ObservationDetailUiState.Loading -> {
                    AppLoader()
                }
                is ObservationDetailUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message, color = Color.Red)
                    }
                }
                is ObservationDetailUiState.Success -> {
                    ObservationDetailContent(detail = state.detail)
                }
            }
        }
    }
}

@Composable
fun ObservationDetailContent(detail: ObservationDetailResponse) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 22.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        Text(
            text = detail.observationTitle ?: "Untitled",
            style = textStyle(size = 22.sp, weight = FontWeight.Bold),
            color = AppColors.Black
        )

        // Date and Time
        if (!detail.date.isNullOrEmpty() || !detail.time.isNullOrEmpty()) {
            Text(
                text = "${detail.date ?: ""} ${detail.time ?: ""}".trim(),
                style = textStyle(size = 14.sp, weight = FontWeight.Normal),
                color = Color.Gray
            )
        }

        // Description
        if (!detail.description.isNullOrEmpty()) {
            Text(
                text = detail.description,
                style = textStyle(size = 16.sp, weight = FontWeight.Normal),
                color = AppColors.BlackText
            )
        }

        // Images Carousel
        if (!detail.imageDescription.isNullOrEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                detail.imageDescription.forEach { img ->
                    if (!img.image.isNullOrEmpty()) {
                        WebImageView(
                            imageUrl = img.image,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }

        // Group / Project Info
        detail.group?.let { group ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = "Project",
                    style = textStyle(size = 16.sp, weight = FontWeight.Bold),
                    color = AppColors.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!group.groupImage.isNullOrEmpty()) {
                        WebImageView(
                            imageUrl = group.groupImage,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                    Column {
                        Text(
                            text = group.groupName ?: "Unknown",
                            style = textStyle(size = 14.sp, weight = FontWeight.SemiBold),
                            color = AppColors.BlackText
                        )
                        if (!group.groupCode.isNullOrEmpty()) {
                            Text(
                                text = group.groupCode,
                                style = textStyle(size = 12.sp, weight = FontWeight.Normal),
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }

        // Responsible Person
        detail.responsiblePerson?.let { person ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = "Responsible Person",
                    style = textStyle(size = 16.sp, weight = FontWeight.Bold),
                    color = AppColors.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!person.image.isNullOrEmpty()) {
                        WebImageView(
                            imageUrl = person.image,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                    Column {
                        Text(
                            text = person.name ?: "Unknown",
                            style = textStyle(size = 14.sp, weight = FontWeight.SemiBold),
                            color = AppColors.BlackText
                        )
                        if (!person.email.isNullOrEmpty()) {
                            Text(
                                text = person.email,
                                style = textStyle(size = 12.sp, weight = FontWeight.Normal),
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }

        // Notified Users
        if (!detail.notificationTo.isNullOrEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = "Notified Users",
                    style = textStyle(size = 16.sp, weight = FontWeight.Bold),
                    color = AppColors.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                detail.notificationTo.forEach { user ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = "• ${user.name ?: user.email ?: "Unknown"}",
                            style = textStyle(size = 14.sp, weight = FontWeight.Normal),
                            color = AppColors.BlackText
                        )
                    }
                }
            }
        }
    }
}
