package org.example.project.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import kotlin.time.Clock
import org.example.project.colors.AppColors
import org.example.project.data.model.InspectionDetailResponse
import org.example.project.typography.textStyle
import org.example.project.ui.components.AppLoader
import org.example.project.ui.components.WebImageView
import org.example.project.utilites.AppBorderButton
import org.example.project.utilites.ErrorRetryView
import org.example.project.utilites.NavigationBackIcon
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

@Composable
fun InspectionDetailScreen(
    inspectionId: Int,
    onBackClicked: () -> Unit
) {
    val viewModel: InspectionDetailViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()
    var showSharePopup by remember { mutableStateOf(false) }
    var previewImageUrl by remember { mutableStateOf<String?>(null) }
    
    val isGeneratingPdf by viewModel.isGeneratingPdf.collectAsState()
    val pdfUrl by viewModel.pdfUrl.collectAsState()
    val fileDownloader = org.example.project.utilites.rememberFileDownloader()

    LaunchedEffect(pdfUrl) {
        pdfUrl?.let { url ->
            try {
                val fileName = "Inspection_PDF_${Clock.System.now().toEpochMilliseconds()}.pdf"
                fileDownloader.downloadFile(url, fileName)
                viewModel.setPdfToastMessage("Downloading Inspection Report")
            } catch (e: Exception) {
                // Handle error
            }
            viewModel.clearPdfUrl()
        }
    }

    LaunchedEffect(inspectionId) {
        viewModel.loadInspectionDetail(inspectionId)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().imePadding(),
        containerColor = Color.White,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (uiState is InspectionDetailUiState.Success) {
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
                            onClick = { viewModel.generatePdf(inspectionId) },
                            modifier = Modifier.weight(1f)
                        )
                        Row(
                            modifier = Modifier.weight(1f)
                                .clickable { showSharePopup = true }
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            Spacer(Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE5E5EA))
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(12.dp))

            Box(modifier = Modifier.weight(1f)) {
                when (val state = uiState) {
                    is InspectionDetailUiState.Idle, is InspectionDetailUiState.Loading -> {
                        AppLoader()
                    }
                    is InspectionDetailUiState.Error -> {
                        ErrorRetryView(
                            errorMessage = state.message,
                            onRetryClick = {
                                viewModel.loadInspectionDetail(inspectionId)
                            }
                        )
                    }
                    is InspectionDetailUiState.Success -> {
                        InspectionDetailContent(
                            detail = state.detail,
                            onImageClick = { previewImageUrl = it }
                        )
                    }
                }
            }
        }
        
        if (showSharePopup) {
            org.example.project.ui.components.SharePopupBottomSheet(
                onDismiss = { showSharePopup = false },
                onTxtClick = { /* Handle TXT share */ },
                onPdfClick = { viewModel.generatePdf(inspectionId) }
            )
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
    }
}

@Composable
fun InspectionDetailContent(
    detail: InspectionDetailResponse,
    onImageClick: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 22.dp, vertical = 16.dp),
    ) {
        // Title Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
                Text(
                    text = "Audit & Inspections",
                    style = textStyle(size = 12.sp, weight = FontWeight.Medium),
                    color = AppColors.TextGray
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = detail.auditItem?.auditItemTitle ?: "Static Equipment Inspection",
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
            text = "Facility / Project",
            style = textStyle(size = 12.sp, weight = FontWeight.Medium),
            color = AppColors.TextGray
        )
        Spacer(Modifier.height(11.dp))
        Row {
            WebImageView(
                imageUrl = detail.facilities?.groupImage,
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

        // Equipment Description
        Text(
            text = "Equipment Description / Model Number..",
            style = textStyle(size = 12.sp, weight = FontWeight.Medium),
            color = AppColors.TextGray
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = detail.modelNumber ?: "-",
            style = textStyle(size = 14.sp, weight = FontWeight.SemiBold),
            color = AppColors.Black
        )

        Spacer(Modifier.height(24.dp))

        // Inspected By
        Text(
            text = "Inspected By",
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
                text = detail.inspectedBy ?: "",
                style = textStyle(size = 14.sp, weight = FontWeight.SemiBold),
                color = AppColors.Black
            )
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

        // Date
        Text(
            text = "Date",
            style = textStyle(size = 12.sp, weight = FontWeight.Medium),
            color = AppColors.TextGray
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = detail.inspectionDate?.takeIf { it.isNotBlank() } ?: "-",
            style = textStyle(size = 14.sp, weight = FontWeight.SemiBold),
            color = AppColors.Black
        )

        Spacer(Modifier.height(24.dp))

        // Description
        Text(
            text = "Description",
            style = textStyle(size = 12.sp, weight = FontWeight.Medium),
            color = AppColors.TextGray
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = detail.translatedDescription
                ?.takeIf { it.isNotBlank() }
                ?: detail.description
                    ?.takeIf { it.isNotBlank() }
                ?: "-",
            style = textStyle(size = 14.sp, weight = FontWeight.Medium),
            color = AppColors.Black
        )

        Spacer(Modifier.height(24.dp))

        // Equipment Source
        Text(
            text = "Equipment Source",
            style = textStyle(size = 12.sp, weight = FontWeight.Medium),
            color = AppColors.TextGray
        )
        Spacer(Modifier.height(4.dp))
        
        val equipmentSourceText = when(detail.equipmentSource) {
            "1" -> "ALNASR"
            "2" -> "Rental"
            "3" -> "Subcontractor" + (detail.subContractor?.let { " ($it)" } ?: "")
            else -> detail.equipmentSource ?: "-"
        }
        
        Text(
            text = equipmentSourceText,
            style = textStyle(size = 14.sp, weight = FontWeight.SemiBold),
            color = AppColors.Black
        )

        Spacer(Modifier.height(24.dp))
        HorizontalDivider(color = Color(0xFFF0F0F5))
        Spacer(Modifier.height(24.dp))

        // Equipment Static Fields
        Text(
            text = "Equipment Static Fields",
            style = textStyle(size = 12.sp, weight = FontWeight.Medium),
            color = AppColors.TextGray
        )
        Spacer(Modifier.height(12.dp))

        if (detail.staticEquipment.isNullOrEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .background(
                        color = Color(0xFFFFF3EE),
                        shape = RoundedCornerShape(4.dp)
                    )
            ) {
                // Left red rectangle
                Box(
                    modifier = Modifier
                        .width(5.dp)
                        .fillMaxHeight()
                        .background(
                            color = AppColors.Primary,
                            shape = RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp)
                        )
                )

                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "No Questions Found!",
                        style = textStyle(size = 14.sp, weight = FontWeight.Bold),
                        color = AppColors.Primary
                    )

                    Text(
                        text = "-",
                        style = textStyle(size = 12.sp, weight = FontWeight.Medium),
                        color = AppColors.Primary
                    )
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                detail.staticEquipment.forEach { question ->
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = question.title ?: "",
                            style = textStyle(size = 13.sp, weight = FontWeight.SemiBold),
                            color = AppColors.Black
                        )
                        val answerText = when(question.selectedValue) {
                            1 -> "YES"
                            2 -> "NO"
                            3 -> "NA"
                            else -> "-"
                        }
                        Text(
                            text = answerText,
                            style = textStyle(size = 13.sp, weight = FontWeight.Medium),
                            color = AppColors.Primary
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        HorizontalDivider(color = Color(0xFFF0F0F5))
        Spacer(Modifier.height(24.dp))

        // Notes
        Text(
            text = "Notes",
            style = textStyle(size = 12.sp, weight = FontWeight.Medium),
            color = AppColors.TextGray
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = detail.translatedNotes
                ?.takeIf { it.isNotBlank() }
                ?: detail.notes
                    ?.takeIf { it.isNotBlank() }
                ?: "-",
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(4.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No Images Uploaded",
                    style = textStyle(size = 14.sp, weight = FontWeight.Medium),
                    color = AppColors.TextGray
                )
            }
        }
        
        Spacer(Modifier.height(40.dp))
    }
}
