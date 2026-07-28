package org.example.project.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import org.example.project.utilites.ToastHost
import org.example.project.utilites.ToastType
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import instaresolv.shared.generated.resources.*
import kotlinx.serialization.json.Json
import org.example.project.colors.AppColors
import org.example.project.data.model.ObservationGroup
import org.example.project.data.model.ObservationItem
import org.example.project.typography.textStyle
import org.example.project.ui.components.AppExitDialog
import org.example.project.utilites.AppBorderButton
import org.example.project.utilites.AppPrimaryButton
import org.example.project.utilites.NavigationBackIcon
import org.example.project.utilites.ToastHost
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun ObservationDraftListScreen(
    onBackClicked: () -> Unit,
    onDraftClicked: (Long) -> Unit
) {
    val viewModel: ObservationListViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()
    var isDeleteOptionEnabled by remember { mutableStateOf(false) }
    var selectedItemsId by remember { mutableStateOf<List<Int>>(emptyList()) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    val draftToastMessage by viewModel.draftToastMessage.collectAsState()
    val successMessage = stringResource(Res.string.draftsDeletedSuccessfully)


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
                NavigationBackIcon(
                    onBackClicked
                )
                Text(
                    text = stringResource(Res.string.drafts).uppercase(),
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
                        .padding(top = 25.dp),
                ) {
                    items(uiState.drafts.size) { index ->
                        val draft = uiState.drafts[index]
                        val group = draft.groupJson?.let {
                            try {
                                Json.decodeFromString<ObservationGroup>(it)
                            } catch(e: Exception) { null }
                        } ?: ObservationGroup(groupName = "Draft", groupCode = "")

                        val images = draft.imageDescriptionsJson.mapNotNull { 
                            try {
                                Json.decodeFromString<org.example.project.data.model.ImageDescriptionRequest>(it).image
                            } catch(e: Exception) { null }
                        }

                        val mappedItem = ObservationItem(
                            observationId = draft.id.toInt(),
                            observationTitle = draft.title,
                            description = draft.description,
                            date = draft.createdAt,
                            status = 1, // Open
                            group = group,
                            images = images,
                            totalImages = images.size
                        )
                        ObservationListItem(
                            observation = mappedItem,
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
                description = stringResource(Res.string.deleteSelectedObservationDrafts),
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
