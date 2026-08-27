package org.example.project.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import org.example.project.utilites.AppPullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_calendar
import instaresolv.shared.generated.resources.ic_category
import org.example.project.colors.AppColors
import org.example.project.data.model.ToolBoxTalkItem
import org.example.project.data.settings.formatDate
import org.example.project.data.settings.timeAgo
import org.example.project.typography.textStyle
import org.example.project.ui.components.AppFilterBottomSheet
import org.example.project.ui.components.AppLoader
import org.example.project.ui.components.WebImageView
import org.example.project.utilites.AppSearchBar
import org.example.project.utilites.ErrorRetryView
import org.example.project.utilites.NavigationBackIcon
import org.example.project.utilites.ToastHost
import org.example.project.utilites.ToastType
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import kotlin.time.Clock
import org.jetbrains.compose.resources.stringResource
import instaresolv.shared.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolBoxTalkListScreen(
    onBackClicked: () -> Unit,
    onCreateClicked: () -> Unit,
    onDraftClicked: () -> Unit = {}
) {
    val viewModel: ToolBoxTalkListViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()
    var showFilterModal by remember { mutableStateOf(false) }
    var selectedToolboxTalkId by remember { mutableStateOf<Int?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val exportUrl by viewModel.exportUrl.collectAsState()
    val exportToastMessage by viewModel.exportToastMessage.collectAsState()
    val fileDownloader = org.example.project.utilites.rememberFileDownloader()

    LaunchedEffect(exportUrl) {
        exportUrl?.let { url ->
            try {
                val fileName = "Toolbox_Talk_Report_${Clock.System.now().toEpochMilliseconds()}.csv"
                fileDownloader.downloadFile(url, fileName)
                viewModel.setExportToastMessage("Downloading Toolbox Talk Report")
            } catch (e: Exception) {
                viewModel.setExportToastMessage(e.message ?: "Export failed")
            }
            viewModel.clearExportUrl()
        }
    }

    Scaffold(
        containerColor = Color.White,
        floatingActionButton = {
            if (uiState.error == null && !uiState.items.isEmpty()) {
                val isExporting by viewModel.isExporting.collectAsState()
                org.example.project.ui.components.excel.CommonExcelButton(
                    isLoading = isExporting,
                    onClick = { viewModel.exportToExcel() }
                )
            }
        },
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
                    text = stringResource(Res.string.toolboxTalks).uppercase(),
                    style = textStyle(
                        size = 14.sp,
                        weight = FontWeight.Bold
                    ),
                    color = AppColors.Black
                )
                Spacer(modifier = Modifier.weight(1f))
                DraftButton(onDraftClicked = onDraftClicked)
                Spacer(modifier = Modifier.width(8.dp))
                NewButton(onNewClicked = { onCreateClicked() })
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 22.dp)
                .background(Color.White)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)
                ) {
                    AppSearchBar(
                        value = uiState.searchKey,
                        onValueChange = {
                            viewModel.updateSearchKey(it)
                        },
                        placeholder = stringResource(Res.string.searchToolboxTalks),
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFF2F2F2))
                            .clickable { showFilterModal = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.ic_category),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        if (!uiState.appliedFilterState.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(AppColors.Primary)
                            )
                        }
                    }
                }

                if (showFilterModal) {
                    AppFilterBottomSheet(
                        appliedFilterState = uiState.appliedFilterState,
                        isFromObservation = false,
                        moduleName = "Toolbox Talks",
                        onApply = { state ->
                            viewModel.applyFilters(state)
                            showFilterModal = false
                        },
                        onDismiss = { showFilterModal = false }
                    )
                }

                if (uiState.isLoading) {
                    AppLoader()
                } else if (uiState.error != null && uiState.items.isEmpty()) {
                    ErrorRetryView(
                        errorMessage = uiState.error ?: "",
                        onRetryClick = { viewModel.fetchToolBoxTalks(isRefresh = true) }
                    )
                } else {
                    AppPullToRefreshBox(
                        isRefreshing = uiState.isPullDown,
                        onRefresh = { viewModel.fetchToolBoxTalks(isPullDown = true) },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (uiState.items.isEmpty()) {
                            EmptyScreenView(
                                message = stringResource(Res.string.noToolboxTalksFound),
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize().padding(top = 25.dp)
                            ) {
                                items(uiState.items.size) { index ->
                                    if (index >= uiState.items.size - 1 && !uiState.isLoading && !uiState.isPaginating && !uiState.endReached) {
                                        LaunchedEffect(key1 = index) {
                                            viewModel.fetchToolBoxTalks(isRefresh = false)
                                        }
                                    }
                                    ToolBoxTalkListItem(
                                        item = uiState.items[index],
                                        onClick = {
                                            selectedToolboxTalkId = uiState.items[index].id
                                        }
                                    )
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

            ToastHost(
                visible = exportToastMessage != null,
                message = exportToastMessage.orEmpty(),
                onDismiss = { viewModel.clearExportToast() },
                type = ToastType.Success,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 20.dp)
            )

            if (selectedToolboxTalkId != null) {
                ModalBottomSheet(
                    onDismissRequest = { selectedToolboxTalkId = null },
                    sheetState = sheetState,
                    containerColor = Color.Transparent,
                    dragHandle = null
                ) {
                    Box(modifier = Modifier.fillMaxHeight(0.9f)) {
                        org.example.project.ui.screens.ToolBoxTalkDetailScreen(
                            id = selectedToolboxTalkId!!,
                            onClose = { selectedToolboxTalkId = null }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ToolBoxTalkListItem(
    item: ToolBoxTalkItem,
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
            Box {
                WebImageView(
                    imageUrl = (item.images?.firstOrNull()?.image ?: "").toString(),
                    modifier = Modifier
                        .width(70.dp)
                        .height(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                )

                item.images?.count()?.let {
                    if (it > 1) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(4.dp)
                                .size(22.dp)
                                .background(Color.Black.copy(alpha = 0.6f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "+${it - 1}",
                                style = textStyle(size = 10.sp, weight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = item.topic ?: "Untitled Toolbox Talk",
                    style = textStyle(size = 15.sp, weight = FontWeight.Bold),
                    color = AppColors.Black,
                    maxLines = 2
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
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
                            text = formatDate(
                                item.date ?: "",
                                inputPattern = "yyyy-MM-dd HH:mm:ss",
                                outputPattern = "dd MMM yyyy"
                            ),
                            style = textStyle(size = 11.sp, weight = FontWeight.SemiBold),
                            color = AppColors.Black
                        )
                    }

                    Text(
                        text = timeAgo(
                            item.createdAt ?: "",
                            inputPattern = "yyyy-MM-dd HH:mm:ss",
                        ), // Need to format real date to relative string if required, using placeholder for now
                        style = textStyle(size = 11.sp, weight = FontWeight.Normal),
                        color = Color.DarkGray
                    )

                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WebImageView(
                        imageUrl = item.facilities?.groupImage ?: "",
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        Text(
                            text = item.facilities?.groupName ?: "",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = textStyle(size = 11.sp, weight = FontWeight.Medium),
                            color = AppColors.Black
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = item.facilities?.groupCode ?: "",
                        maxLines = 1,
                        style = textStyle(size = 10.sp, weight = FontWeight.Medium),
                        color = AppColors.Black
                    )
                }
            }
        }
        HorizontalDivider()
    }
}
