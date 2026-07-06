package org.example.project.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_translate
import instaresolv.shared.generated.resources.ic_avatar
import instaresolv.shared.generated.resources.ic_share
import org.example.project.colors.AppColors
import org.example.project.data.model.ToolBoxTalkItem
import org.example.project.data.settings.formatDate
import org.example.project.typography.textStyle
import org.example.project.ui.components.AppLoader
import org.example.project.ui.components.WebImageView
import org.example.project.utilites.AppBorderButton
import org.example.project.utilites.ErrorRetryView
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

@Composable
fun ToolBoxTalkDetailScreen(
    id: Int,
    onClose: () -> Unit
) {
    val viewModel: ToolBoxTalkDetailViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()
    
    var previewImageUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(id) {
        viewModel.loadToolBoxTalkDetail(id)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().imePadding(),
        containerColor = Color.White,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (uiState is ToolBoxTalkDetailUiState.Success) {
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
                            onClick = {
                                // PDF generation block
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
    ) { paddingValues ->
        when (val state = uiState) {
            is ToolBoxTalkDetailUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(top = 40.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    AppLoader()
                }
            }
            is ToolBoxTalkDetailUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(top = 40.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    ErrorRetryView(
                        errorMessage = state.message,
                        onRetryClick = { viewModel.loadToolBoxTalkDetail(id) }
                    )
                }
            }
            is ToolBoxTalkDetailUiState.Success -> {
                val data = state.data
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .padding(paddingValues)
                        .padding(top = 40.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header Handle
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(4.dp)
                                .background(Color.LightGray, RoundedCornerShape(2.dp))
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
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
                    }

                    Column(modifier = Modifier.padding(horizontal = 22.dp)) {
                        // Topic
                        Text(
                            text = "Topic",
                            style = textStyle(12.sp, FontWeight.Normal),
                            color = AppColors.TextGray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = data.topic ?: "N/A",
                            style = textStyle(16.sp, FontWeight.Bold),
                            color = AppColors.Black
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Facility / Project
                        Text(
                            text = "Facility / Project",
                            style = textStyle(12.sp, FontWeight.Normal),
                            color = AppColors.TextGray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        data.facilities?.let { fac ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                WebImageView(
                                    imageUrl = fac.groupImage ?: "",
                                    modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp))
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = fac.groupName ?: "N/A",
                                        style = textStyle(14.sp, FontWeight.SemiBold),
                                        color = AppColors.Black
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .background(AppColors.TextGray.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = fac.groupCode ?: "",
                                            style = textStyle(10.sp, FontWeight.Medium),
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF0F0F0))

                        // Reported By
                        Text(
                            text = "Reported By",
                            style = textStyle(12.sp, FontWeight.Normal),
                            color = AppColors.TextGray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_avatar),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color.Unspecified
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = data.reportedBy ?: "N/A",
                                style = textStyle(14.sp, FontWeight.SemiBold),
                                color = AppColors.Black
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Date
                        Text(
                            text = "Date",
                            style = textStyle(12.sp, FontWeight.Normal),
                            color = AppColors.TextGray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = data.date?.let { formatDate(it, "yyyy-MM-dd HH:mm:ss", "dd MMM yyyy") } ?: "N/A",
                            style = textStyle(14.sp, FontWeight.SemiBold),
                            color = AppColors.Black
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Start & End Time
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Start Time",
                                    style = textStyle(12.sp, FontWeight.Normal),
                                    color = AppColors.TextGray
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = data.startTime ?: "N/A",
                                    style = textStyle(14.sp, FontWeight.SemiBold),
                                    color = AppColors.Black
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "End Time",
                                    style = textStyle(12.sp, FontWeight.Normal),
                                    color = AppColors.TextGray
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = data.endTime ?: "N/A",
                                    style = textStyle(14.sp, FontWeight.SemiBold),
                                    color = AppColors.Black
                                )
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF0F0F0))

                        // Discussion Points
                        if (!data.discussionPoints.isNullOrEmpty()) {
                            Text(
                                text = "Discussion Points",
                                style = textStyle(14.sp, FontWeight.Bold),
                                color = AppColors.Primary
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            data.discussionPoints.forEachIndexed { index, dp ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(
                                        text = "${index + 1}. ",
                                        style = textStyle(12.sp, FontWeight.SemiBold),
                                        color = AppColors.Black
                                    )
                                    Text(
                                        text = dp.point ?: "",
                                        style = textStyle(12.sp, FontWeight.Normal),
                                        color = AppColors.Black
                                    )
                                }
                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF0F0F0))
                        }

                        // Attendees
                        if (!data.attendees.isNullOrEmpty()) {
                            val attendeesList = data.attendees
                            Text(
                                text = "ATTENDEES (${attendeesList.size})",
                                style = textStyle(12.sp, FontWeight.Bold),
                                color = AppColors.BlackText
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            // Table Header
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color(0xFFE5E5E5), RoundedCornerShape(8.dp))
                                    .background(Color.White, RoundedCornerShape(8.dp))
                                    .clip(RoundedCornerShape(8.dp))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState())
                                ) {
                                    Column {
                                        Row(
                                            modifier = Modifier
                                                .background(Color(0xFFF9F9FB))
                                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                        ) {
                                            Text("Employee Code", style = textStyle(12.sp, FontWeight.Medium), color = AppColors.TextGray, modifier = Modifier.width(120.dp))
                                            Text("Name", style = textStyle(12.sp, FontWeight.Medium), color = AppColors.TextGray, modifier = Modifier.width(180.dp))
                                            Text("Company", style = textStyle(12.sp, FontWeight.Medium), color = AppColors.TextGray, modifier = Modifier.width(150.dp))
                                            Text("Profession", style = textStyle(12.sp, FontWeight.Medium), color = AppColors.TextGray, modifier = Modifier.width(150.dp))
                                        }

                                        attendeesList.forEachIndexed { index, attendee ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(if (index % 2 == 0) Color.White else Color(0xFFF9F9FB))
                                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(attendee.employeeCode ?: "-", style = textStyle(12.sp, FontWeight.Normal), color = AppColors.Black, modifier = Modifier.width(120.dp))
                                                Text(attendee.employeeName ?: "-", style = textStyle(12.sp, FontWeight.Normal), color = AppColors.Black, modifier = Modifier.width(180.dp))
                                                Text(attendee.companyName ?: "-", style = textStyle(12.sp, FontWeight.Normal), color = AppColors.Black, modifier = Modifier.width(150.dp))
                                                Text(attendee.profession ?: "-", style = textStyle(12.sp, FontWeight.Normal), color = AppColors.Black, modifier = Modifier.width(150.dp))
                                            }
                                        }
                                    }
                                }
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF0F0F0))
                        }

                        // Evidence
                        if (!data.images.isNullOrEmpty()) {
                            val imagesList = data.images
                            Text(
                                text = "ATTENDEES EVIDENCE",
                                style = textStyle(12.sp, FontWeight.Bold),
                                color = AppColors.BlackText
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Uploaded Images",
                                style = textStyle(12.sp, FontWeight.Medium),
                                color = AppColors.TextGray
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                imagesList.forEach { imageDetail ->
                                    if (imageDetail.image?.isNotBlank() == true) {
                                        Column {
                                            WebImageView(
                                                imageUrl = imageDetail.image,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(200.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .clickable { previewImageUrl = imageDetail.image },
                                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                            )
                                            if (!imageDetail.description.isNullOrEmpty()) {
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(
                                                    text = imageDetail.description,
                                                    style = textStyle(14.sp, FontWeight.Normal),
                                                    color = AppColors.Black
                                                )
                                            }
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

        if (previewImageUrl != null) {
            org.example.project.ui.components.AppImagePreviewDialog(
                imageUrl = previewImageUrl!!,
                onDismiss = { previewImageUrl = null }
            )
        }
    }
}
