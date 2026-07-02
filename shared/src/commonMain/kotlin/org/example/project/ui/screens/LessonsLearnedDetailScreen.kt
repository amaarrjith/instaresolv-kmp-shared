package org.example.project.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.colors.AppColors
import org.example.project.typography.textStyle
import org.example.project.ui.components.AppLoader
import org.example.project.ui.components.WebImageView
import org.example.project.utilites.ErrorRetryView
import org.koin.compose.koinInject

@Composable
fun LessonsLearnedDetailScreen(
    id: Int,
    onClose: () -> Unit
) {
    val viewModel: LessonsLearnedDetailViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()
    
    var previewImageUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(id) {
        viewModel.loadLessonLearnedDetail(id)
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        when (val state = uiState) {
            is LessonsLearnedDetailUiState.Loading -> {
                AppLoader(modifier = Modifier.fillMaxSize())
            }
            is LessonsLearnedDetailUiState.Error -> {
                ErrorRetryView(
                    message = state.message,
                    onRetry = { viewModel.loadLessonLearnedDetail(id) }
                )
            }
            is LessonsLearnedDetailUiState.Success -> {
                val data = state.data
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Header
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                WebImageView(
                                    imageUrl = data.facilities?.groupImage ?: "",
                                    modifier = Modifier.size(48.dp).clip(CircleShape)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = data.facilities?.groupName ?: "-",
                                        style = textStyle(16.sp, FontWeight.SemiBold),
                                        color = AppColors.Black
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = data.facilities?.groupCode ?: "-",
                                        style = textStyle(12.sp, FontWeight.Normal),
                                        color = AppColors.TextGray
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            // Title
                            Text(
                                text = "TITLE",
                                style = textStyle(12.sp, FontWeight.Normal),
                                color = AppColors.TextGray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = data.title ?: "-",
                                style = textStyle(14.sp, FontWeight.Medium),
                                color = AppColors.Black
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Description
                            Text(
                                text = "DESCRIPTION",
                                style = textStyle(12.sp, FontWeight.Normal),
                                color = AppColors.TextGray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = data.description ?: "-",
                                style = textStyle(14.sp, FontWeight.Medium),
                                color = AppColors.Black
                            )
                            
                            if (!data.translatedDescription.isNullOrEmpty()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "TRANSLATED DESCRIPTION",
                                    style = textStyle(12.sp, FontWeight.Normal),
                                    color = AppColors.TextGray
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = data.translatedDescription,
                                    style = textStyle(14.sp, FontWeight.Medium),
                                    color = AppColors.Black
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Reported By
                            Text(
                                text = "REPORTED BY",
                                style = textStyle(12.sp, FontWeight.Normal),
                                color = AppColors.TextGray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = data.reportedBy ?: "-",
                                style = textStyle(14.sp, FontWeight.Medium),
                                color = AppColors.Black
                            )
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            // Images
                            if (data.images?.isNotEmpty() == true) {
                                Text(
                                    text = "Uploaded Images",
                                    style = textStyle(12.sp, FontWeight.Medium),
                                    color = AppColors.TextGray
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    data.images.forEach { imageDetail ->
                                        Column {
                                            if (!imageDetail.image.isNullOrEmpty()) {
                                                WebImageView(
                                                    imageUrl = imageDetail.image,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(200.dp)
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .clickable { previewImageUrl = imageDetail.image },
                                                    contentScale = ContentScale.Crop
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                            }
                                            if (!imageDetail.description.isNullOrEmpty()) {
                                                Text(
                                                    text = imageDetail.description,
                                                    style = textStyle(14.sp, FontWeight.Normal),
                                                    color = AppColors.Black
                                                )
                                            }
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(32.dp))
                            }
                        }
                    }
                }
            }
        }
    }
    
    previewImageUrl?.let { url ->
        org.example.project.ui.components.AppImagePreviewDialog(
            imageUrl = url,
            onDismiss = { previewImageUrl = null }
        )
    }
}
