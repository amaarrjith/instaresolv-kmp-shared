package org.example.project.ui.screens

import org.example.project.ui.components.AppFilterBottomSheet
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.width
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import instaresolv.shared.generated.resources.ic_add
import instaresolv.shared.generated.resources.ic_edit
import instaresolv.shared.generated.resources.ic_email
import instaresolv.shared.generated.resources.ic_calendar
import instaresolv.shared.generated.resources.ic_checkbox_on
import instaresolv.shared.generated.resources.ic_checkbox_off
import instaresolv.shared.generated.resources.ic_category
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import org.koin.compose.koinInject
import org.example.project.data.model.FilterContentData
import org.example.project.data.model.ObservationItem
import org.example.project.ui.components.AppLoader
import org.example.project.utilites.ErrorRetryView
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_download
import instaresolv.shared.generated.resources.ic_export
import instaresolv.shared.generated.resources.ic_filter
import org.example.project.colors.AppColors
import org.example.project.data.settings.formatDate
import org.example.project.data.settings.timeAgo
import org.example.project.typography.textStyle
import org.example.project.ui.ObservationStatus
import org.example.project.ui.components.WebImageView
import org.example.project.utilites.AppPrimaryButton
import org.example.project.utilites.AppSearchBar
import org.example.project.utilites.NavigationBackIcon
import org.example.project.utilites.ToastHost
import org.jetbrains.compose.resources.painterResource
import kotlin.time.Clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObservationListScreen(
    onBackClicked: () -> Unit,
    onCreateClicked: () -> Unit
) {
    val viewModel: ObservationListViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()
    var showFilterModal by remember { mutableStateOf(false) }
    var selectedObservationId by remember { mutableStateOf<Int?>(null) }
    val exportUrl by viewModel.exportUrl.collectAsState()
    val fileDownloader = org.example.project.utilites.rememberFileDownloader()

    LaunchedEffect(exportUrl) {
        exportUrl?.let { url ->
            try {
                val fileName = "Observation_Report_${Clock.System.now().toEpochMilliseconds()}.csv"
                fileDownloader.downloadFile(url, fileName)
                viewModel.setExportToastMessage("Downloading Observation Report")
            } catch (e: Exception) {
                uiState.errorExcel = e.message
            }
            viewModel.clearExportUrl()
        }
    }

    Scaffold(
        containerColor = Color.White,
        floatingActionButton = {
            val isExporting by viewModel.isExporting.collectAsState()
            org.example.project.ui.components.excel.CommonExcelButton(
                isLoading = isExporting,
                onClick = { viewModel.exportToExcel() }
            )
        },
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
                    text = "Observations".uppercase(),
                    style = textStyle(
                        size = 14.sp,
                        weight = FontWeight.Bold
                    ),
                    color = AppColors.Black
                )
                Spacer(modifier = Modifier.weight(1f))
                DraftButton(onDraftClicked = {})
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
            val exportToastMessage by viewModel.exportToastMessage.collectAsState()
            
            Column() {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().height(androidx.compose.foundation.layout.IntrinsicSize.Min)
                ) {
                    AppSearchBar(
                        value = uiState.searchKey,
                        onValueChange = {
                            viewModel.updateSearchKey(it)
                        },
                        placeholder = "Search Observations",
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
                        filterData = uiState.filterData,
                        appliedFilterState = uiState.appliedFilterState,
                        isFromObservation = true,
                        moduleName = "Observations",
                        onApply = { state -> 
                            viewModel.applyFilters(state)
                            showFilterModal = false
                        },
                        onDismiss = { showFilterModal = false }
                    )
                }

                if (selectedObservationId != null) {
                    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                    ModalBottomSheet(
                        onDismissRequest = { selectedObservationId = null },
                        sheetState = sheetState,
                        containerColor = Color.White,
                        dragHandle = null
                    ) {
                        Box(modifier = Modifier.fillMaxHeight(0.9f)) {
                            org.example.project.ui.screens.ObservationDetailScreen(
                                observationId = selectedObservationId!!,
                                onBackClicked = { selectedObservationId = null },
                                onRefreshList =  {
                                    viewModel.fetchObservations(isRefresh = true)
                                }
                            )
                        }
                    }
                }

                if (uiState.isLoading && uiState.observations.isEmpty()) {
                    AppLoader()
                } else if (uiState.error != null && uiState.observations.isEmpty()) {
                    ErrorRetryView(
                        errorMessage = uiState.error ?: "",
                        onRetryClick = { viewModel.fetchObservations(isRefresh = true) }
                    )
                } else {
                    PullToRefreshBox(
                        isRefreshing = uiState.isLoading,
                        onRefresh = { viewModel.fetchObservations(isRefresh = true) },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (uiState.observations.isEmpty()) {
                           EmptyScreenView(
                                message = "No observations found",
                           )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize()
                                    .padding(top = 25.dp),
                            ) {
                                items(uiState.observations.size) { index ->
                                    if (index >= uiState.observations.size - 1 && !uiState.isLoading && !uiState.isPaginating && !uiState.endReached) {
                                        LaunchedEffect(key1 = index) {
                                            viewModel.fetchObservations(isRefresh = false)
                                        }
                                    }
                                    ObservationListItem(
                                        observation = uiState.observations[index],
                                        onClick = { selectedObservationId = uiState.observations[index].observationId }
                                    )
                                }
                                if (uiState.isPaginating) {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
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
                visible = uiState.errorExcel != null,
                message = uiState.errorExcel  ?: "",
                onDismiss = { uiState.errorExcel = null },
                type = org.example.project.utilites.ToastType.Success,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 20.dp)
            )
            ToastHost(
                visible = exportToastMessage != null,
                message = exportToastMessage.orEmpty(),
                onDismiss = { viewModel.clearExportToast() },
                type = org.example.project.utilites.ToastType.Success,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 20.dp)
            )
        }
    }
}

@Composable
fun DraftButton(
    onDraftClicked: () -> Unit
) {
    Box(
        modifier = Modifier.clickable {
            onDraftClicked()
        }
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(AppColors.SkyBlue.copy(alpha = 0.1f))
                .padding(horizontal = 14.dp)
                .padding(vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_edit),
                contentDescription = null
            )
            Text(
                text = "Drafts",
                style = textStyle(
                    size = 13.sp,
                    weight = FontWeight.Bold
                ),
                color = AppColors.SkyBlue
            )
        }
    }
}

@Composable
fun NewButton(
    onNewClicked: () -> Unit
) {
    Box(
        modifier = Modifier.clickable {
            onNewClicked()
        }
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(AppColors.Primary.copy(alpha = 0.1f))
                .padding(horizontal = 14.dp)
                .padding(vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_add),
                contentDescription = null
            )
            Text(
                text = "New",
                style = textStyle(
                    size = 13.sp,
                    weight = FontWeight.Bold
                ),
                color = AppColors.Primary
            )
        }
    }
}

@Composable
fun ObservationListItem(
    observation: ObservationItem,
    onClick: () -> Unit
) {
    Column(
    ) {
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
                    imageUrl = observation.images.firstOrNull() ?: "", // using first image if available
                    modifier = Modifier
                        .width(70.dp)
                        .height(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                
                if (observation.totalImages > 1) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(4.dp)
                            .size(22.dp)
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+${observation.totalImages - 1}",
                            style = textStyle(size = 10.sp, weight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                // Top Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val status = ObservationStatus.fromId(observation.status ?: -1)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(status.backgroundColor)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = status.title.uppercase(),
                            style = textStyle(size = 9.sp, weight = FontWeight.Bold),
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = timeAgo(
                            observation.date ?: "",
                            inputPattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'",
                        ), // Need to format real date to relative string if required, using placeholder for now
                        style = textStyle(size = 11.sp, weight = FontWeight.Normal),
                        color = Color.DarkGray
                    )
                }
                Row() {
                    Image(
                        painter = painterResource(Res.drawable.ic_calendar),
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        colorFilter = ColorFilter.tint(Color.Gray)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = formatDate(
                            observation.date ?: "",
                            inputPattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'",
                            outputPattern = "dd MMM yyyy"
                        ),
                        style = textStyle(size = 11.sp, weight = FontWeight.SemiBold),
                        color = AppColors.Black
                    )
                }
                // Title
                Text(
                    text = observation.observationTitle ?: "Untitled Observation",
                    style = textStyle(size = 15.sp, weight = FontWeight.Bold),
                    color = AppColors.Black,
                    maxLines = 2
                )
                // User row
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WebImageView(
                        imageUrl = observation.group?.groupImage
                            ?: "", // dummy image
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = observation.group?.groupName ?: "",
                        style = textStyle(size = 11.sp, weight = FontWeight.Medium),
                        color = AppColors.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = observation.group?.groupCode ?: "",
                        style = textStyle(size = 11.sp, weight = FontWeight.Medium),
                        color = AppColors.Black
                    )

                }
            }
        }
        HorizontalDivider()
    }
}


@Composable
@Preview
fun ObservationListScreenPreview() {
    ObservationListScreen(
        onBackClicked = {

        },
        onCreateClicked = {

        }
    )
}