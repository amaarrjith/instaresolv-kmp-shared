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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import instaresolv.shared.generated.resources.*
import kotlinx.serialization.json.Json
import org.example.project.colors.AppColors
import org.example.project.data.model.Project
import org.example.project.data.model.LocalViolationImage
import org.example.project.data.settings.formatDate
import org.example.project.shared.db.ViolationDraft
import org.example.project.typography.textStyle
import org.example.project.ui.components.AppExitDialog
import org.example.project.utilites.AppBorderButton
import org.example.project.utilites.AppPrimaryButton
import org.example.project.utilites.NavigationBackIcon
import org.example.project.utilites.ToastHost
import org.example.project.utilites.ToastType
import org.example.project.ui.components.WebImageView
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun ViolationDraftListScreen(
    onBackClicked: () -> Unit,
    onDraftClicked: (Long) -> Unit
) {
    val viewModel: ViolationListViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()
    var isDeleteOptionEnabled by remember { mutableStateOf(false) }
    var selectedItemsId by remember { mutableStateOf<List<Int>>(emptyList()) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    val draftToastMessage by viewModel.draftToastMessage.collectAsState()
    val successMessage = "Violation drafts deleted successfully."

    LaunchedEffect(Unit) {
        viewModel.loadDrafts()
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            Row (
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(vertical = 10.dp)
                    .padding(end = 22.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavigationBackIcon(onBackClicked)
                Text(
                    text = "VIOLATION DRAFTS",
                    style = textStyle(
                        size = 14.sp,
                        weight = FontWeight.Bold
                    ),
                    color = AppColors.Black
                )
                Spacer(modifier = Modifier.weight(1f))
                if (uiState.drafts.isNotEmpty()) {
                    Image(
                        painter = painterResource(Res.drawable.ic_trash),
                        contentDescription = null,
                        modifier = Modifier
                            .size(30.dp)
                            .clickable {
                                isDeleteOptionEnabled = !isDeleteOptionEnabled
                                if (!isDeleteOptionEnabled) {
                                    selectedItemsId = emptyList()
                                }
                            }
                    )
                }
            }
        },
        bottomBar = {
            if (isDeleteOptionEnabled) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 22.dp, vertical = 10.dp)
                ) {
                    Box(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                        AppBorderButton(
                            title = stringResource(Res.string.cancel),
                            onClick = { 
                                isDeleteOptionEnabled = false
                                selectedItemsId = emptyList()
                            },
                        )
                    }
                    Box(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                        AppPrimaryButton(
                            title = buildString {
                                append(stringResource(Res.string.delete))
                                if (selectedItemsId.isNotEmpty()) {
                                    append(" (${selectedItemsId.size})")
                                }
                            },
                            onClick = { 
                                if (selectedItemsId.isNotEmpty()) {
                                    showDeleteDialog = true
                                }
                            },
                            enabled = selectedItemsId.isNotEmpty()
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 22.dp)
        ) {
            if (uiState.drafts.isEmpty()) {
                EmptyScreenView(
                    message = "No drafts found",
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    items(uiState.drafts.size) { index ->
                        val draft = uiState.drafts[index]
                        val project = draft.facilityJson?.let {
                            try {
                                Json.decodeFromString<Project>(it)
                            } catch(e: Exception) { null }
                        }

                        val images = draft.imagesJson.mapNotNull { 
                            try {
                                Json.decodeFromString<LocalViolationImage>(it).imageUrl
                            } catch(e: Exception) { null }
                        }

                        ViolationDraftListItem(
                            draft = draft,
                            project = project,
                            imageUrl = images.firstOrNull(),
                            isSelectionMode = isDeleteOptionEnabled,
                            isSelected = selectedItemsId.contains(draft.id.toInt()),
                            onSelectionChange = { checked ->
                                selectedItemsId = if (checked) {
                                    selectedItemsId + draft.id.toInt()
                                } else {
                                    selectedItemsId - draft.id.toInt()
                                }
                            },
                            onClick = { 
                                if (isDeleteOptionEnabled) {
                                    val checked = !selectedItemsId.contains(draft.id.toInt())
                                    selectedItemsId = if (checked) {
                                        selectedItemsId + draft.id.toInt()
                                    } else {
                                        selectedItemsId - draft.id.toInt()
                                    }
                                } else {
                                    onDraftClicked(draft.id)
                                }
                            }
                        )
                    }
                }
            }
            
            AppExitDialog(
                visible = showDeleteDialog,
                title = stringResource(Res.string.delete),
                description = "Are you sure you want to delete the selected violation drafts?",
                primaryButtonText = stringResource(Res.string.yes),
                secondaryButtonText = stringResource(Res.string.no),
                onConfirm = {
                    showDeleteDialog = false
                    isDeleteOptionEnabled = false
                    viewModel.deleteDrafts(
                        ids = selectedItemsId.map { it.toLong() },
                        successMessage = successMessage
                    )
                    selectedItemsId = emptyList()
                },
                onDismiss = {
                    showDeleteDialog = false
                }
            )

            ToastHost(
                visible = draftToastMessage != null,
                message = draftToastMessage.orEmpty(),
                onDismiss = { viewModel.clearDraftToast() },
                type = ToastType.Success,
            )
        }
    }
}

@Composable
fun ViolationDraftListItem(
    draft: ViolationDraft,
    project: Project?,
    imageUrl: String?,
    isSelectionMode: Boolean,
    isSelected: Boolean,
    onSelectionChange: (Boolean) -> Unit,
    onClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (isSelectionMode) {
                Image(
                    painter = painterResource(if (isSelected) Res.drawable.ic_checkbox_on else Res.drawable.ic_checkbox_off),
                    contentDescription = null,
                    modifier = Modifier
                        .size(22.dp)
                        .align(Alignment.CenterVertically)
                        .clickable { onSelectionChange(!isSelected) }
                )
            }

            Box {
                WebImageView(
                    imageUrl = imageUrl ?: "",
                    modifier = Modifier
                        .width(70.dp)
                        .height(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = "Violation by ${draft.employeeName.takeIf { it.isNotBlank() } ?: "Unknown"}",
                    style = textStyle(size = 15.sp, weight = FontWeight.Bold),
                    color = AppColors.Black,
                    maxLines = 3
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.ic_calendar),
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            colorFilter = ColorFilter.tint(Color.Gray)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = if (draft.violationDate.isNotBlank()) {
                                formatDate(
                                    draft.violationDate,
                                    inputPattern = "dd-MM-yyyy",
                                    outputPattern = "dd MMM yyyy"
                                )
                            } else "Date not set",
                            style = textStyle(size = 11.sp, weight = FontWeight.SemiBold),
                            color = AppColors.Black
                        )
                    }
                }
                
                if (project != null) {
                    Spacer(modifier = Modifier.height(5.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        WebImageView(
                            imageUrl = project.groupImage ?: "",
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier.weight(1f, fill = false)
                        ) {
                            Text(
                                text = project.groupName ?: "",
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                style = textStyle(
                                    size = 11.sp,
                                    weight = FontWeight.Medium
                                ),
                                color = AppColors.Black
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = project.groupCode ?: "",
                            maxLines = 1,
                            style = textStyle(
                                size = 9.sp,
                                weight = FontWeight.Medium
                            ),
                            color = AppColors.Black
                        )
                    }
                }
            }
        }
        HorizontalDivider()
    }
}
