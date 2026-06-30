package org.example.project.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_share
import instaresolv.shared.generated.resources.ic_translate
import org.example.project.colors.AppColors
import org.example.project.data.model.ViolationData
import org.example.project.data.settings.formatDate
import org.example.project.typography.textStyle
import org.example.project.ui.components.AppLoader
import org.example.project.ui.components.WebImageView
import org.example.project.utilites.AppBorderButton
import org.example.project.utilites.ErrorRetryView
import org.example.project.utilites.ToastHost
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import kotlin.time.Clock

@Composable
fun ViolationDetailScreen(
    violationId: Int,
    onBackClicked: () -> Unit,
    onRefreshList: () -> Unit = {}
) {
    val viewModel: ViolationDetailViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()
    var previewImageUrl by remember { mutableStateOf<String?>(null) }

    val isGeneratingPdf by viewModel.isGeneratingPdf.collectAsState()
    val pdfUrl by viewModel.pdfUrl.collectAsState()
    val pdfToastMessage by viewModel.pdfToastMessage.collectAsState()
    val fileDownloader = org.example.project.utilites.rememberFileDownloader()

    LaunchedEffect(pdfUrl) {
        pdfUrl?.let { url ->
            try {
                val fileName = "Violation_PDF_${Clock.System.now().toEpochMilliseconds()}.pdf"
                fileDownloader.downloadFile(url, fileName)
                viewModel.setPdfToastMessage("Downloading Violation Report")
            } catch (e: Exception) {
                // Handle error
            }
            viewModel.clearPdfUrl()
        }
    }

    LaunchedEffect(violationId) {
        viewModel.loadViolationDetail(violationId)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().imePadding(),
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (uiState.detail != null && !uiState.isLoading) {
                Surface(color = Color.White, shadowElevation = 8.dp) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 22.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        AppBorderButton(
                            title = "Generate PDF",
                            onClick = { viewModel.generatePdf(violationId) },
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
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(top = 40.dp)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(Color.White)
        ) {
            when {
                uiState.isLoading -> {
                    AppLoader()
                }
                uiState.error != null -> {
                    ErrorRetryView(
                        errorMessage = uiState.error ?: "",
                        onRetryClick = {
                            viewModel.loadViolationDetail(violationId)
                        }
                    )
                }
                uiState.detail != null -> {
                    ViolationDetailContent(
                        detail = uiState.detail!!,
                        onImageClick = { previewImageUrl = it }
                    )
                }
                else -> {
                    AppLoader()
                }
            }

            if (isGeneratingPdf) {
                org.example.project.ui.components.PdfGenerationLoader()
            }
            
            previewImageUrl?.let { url ->
                org.example.project.ui.components.AppImagePreviewDialog(
                    imageUrl = url,
                    onDismiss = { previewImageUrl = null }
                )
            }
            
            ToastHost(
                visible = pdfToastMessage != null,
                message = pdfToastMessage.orEmpty(),
                onDismiss = { viewModel.clearPdfToastMessage() },
                type = org.example.project.utilites.ToastType.Success,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 20.dp)
            )
        }
    }
}

@Composable
fun ViolationDetailContent(
    detail: ViolationData,
    onImageClick: (String) -> Unit
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

        // Title Row
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
                    text = "Violation by ${detail.employeeName ?: "Unknown"}",
                    style = textStyle(size = 16.sp, weight = FontWeight.Bold),
                    color = AppColors.Black
                )
            }
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFF8F9098)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(Res.drawable.ic_translate),
                    contentDescription = null,
                )
            }
        }
        Spacer(Modifier.height(24.dp))

        // Project
        Text(
            text = "Project",
            style = textStyle(size = 12.sp, weight = FontWeight.Medium),
            color = AppColors.TextGray
        )
        Spacer(Modifier.height(11.dp))
        Row {
            WebImageView(
                imageUrl = detail.facilities?.groupImage ?: "",
                modifier = Modifier.size(42.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = detail.facilities?.groupName ?: "",
                    style = textStyle(size = 13.sp, weight = FontWeight.SemiBold),
                    color = AppColors.Black
                )
                if (!detail.facilities?.groupCode.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Gray)
                    ) {
                        Text(
                            text = detail.facilities?.groupCode ?: "",
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
        }

        Spacer(Modifier.height(24.dp))
        HorizontalDivider(color = Color(0xFFF0F0F5))
        Spacer(Modifier.height(24.dp))

        // Reported By
        Text(
            text = "Reported By",
            style = textStyle(size = 12.sp, weight = FontWeight.Medium),
            color = AppColors.TextGray
        )
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            WebImageView(
                imageUrl = "",
                modifier = Modifier.size(25.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = detail.reportedBy ?: "-",
                style = textStyle(size = 14.sp, weight = FontWeight.SemiBold),
                color = AppColors.Black
            )
        }

        Spacer(Modifier.height(24.dp))
        
        // Employee Name
        Text(
            text = "Employee Name",
            style = textStyle(size = 12.sp, weight = FontWeight.Medium),
            color = AppColors.TextGray
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = detail.employeeName ?: "-",
            style = textStyle(size = 14.sp, weight = FontWeight.SemiBold),
            color = AppColors.Black
        )

        Spacer(Modifier.height(24.dp))

        // Employee ID and Violation Date Row
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Employee ID",
                    style = textStyle(size = 12.sp, weight = FontWeight.Medium),
                    color = AppColors.TextGray
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = detail.employeeId ?: "-",
                    style = textStyle(size = 14.sp, weight = FontWeight.SemiBold),
                    color = AppColors.Black
                )

            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Violation Date",
                    style = textStyle(size = 12.sp, weight = FontWeight.Medium),
                    color = AppColors.TextGray
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = formatDate(detail.violationDate ?: "", inputPattern = "dd-MM-yyyy", outputPattern = "dd MMM yyyy"),
                    style = textStyle(size = 14.sp, weight = FontWeight.SemiBold),
                    color = AppColors.Black
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // Location
        Text(
            text = "Location",
            style = textStyle(size = 12.sp, weight = FontWeight.Medium),
            color = AppColors.TextGray
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = detail.location ?: "-",
            style = textStyle(size = 14.sp, weight = FontWeight.SemiBold),
            color = AppColors.Black
        )

        Spacer(Modifier.height(24.dp))
        HorizontalDivider(color = Color(0xFFF0F0F5))
        Spacer(Modifier.height(24.dp))

        // Description
        Text(
            text = "Description (AI Translated)",
            style = textStyle(size = 12.sp, weight = FontWeight.Medium),
            color = AppColors.TextGray
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = detail.translatedDescription ?: detail.description ?: "-",
            style = textStyle(size = 14.sp, weight = FontWeight.Medium),
            color = AppColors.Black
        )

        Spacer(Modifier.height(24.dp))

        // Uploaded Images
        Text(
            text = "Uploaded Images",
            style = textStyle(size = 12.sp, weight = FontWeight.Medium),
            color = AppColors.TextGray
        )
        Spacer(Modifier.height(12.dp))

        if (!detail.images.isNullOrEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                detail.images.forEach { img ->
                    Column {
                        if (!img.image.isNullOrEmpty()) {
                            WebImageView(
                                imageUrl = img.image,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onImageClick(img.image) },
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
                "No Images Uploaded"
            )
        }
        
        Spacer(Modifier.height(40.dp))
    }
}
