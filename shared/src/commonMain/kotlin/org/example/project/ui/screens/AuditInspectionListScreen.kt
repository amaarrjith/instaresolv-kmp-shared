package org.example.project.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_calendar
import instaresolv.shared.generated.resources.ic_category
import instaresolv.shared.generated.resources.ic_location
import org.example.project.colors.AppColors
import org.example.project.data.model.InspectionData
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
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import instaresolv.shared.generated.resources.ic_right_icon
import kotlin.time.Clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuditInspectionListScreen(
    onBackClicked: () -> Unit,
    onCreateClicked: (Int, String) -> Unit = { _, _ -> },
    onItemClicked: (Int) -> Unit = {}
) {
    val viewModel: AuditInspectionListViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()
    var showFilterModal by remember { mutableStateOf(false) }
    var showAddModal by remember { mutableStateOf(false) }
    var selectedInspectionId by remember { mutableStateOf<Int?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val exportUrl by viewModel.exportUrl.collectAsState()
    val fileDownloader = org.example.project.utilites.rememberFileDownloader()

    LaunchedEffect(exportUrl) {
        exportUrl?.let { url ->
            try {
                // Generate a name from url or date
                val fileName = "Inspection_Report_${Clock.System.now().toEpochMilliseconds()}.csv"
                fileDownloader.downloadFile(url, fileName)
                viewModel.setExportToastMessage("Downloading Inspection Report")
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
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(vertical = 10.dp)
                    .padding(end = 22.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavigationBackIcon(onBackClicked)
                Text(
                    text = "Inspections".uppercase(),
                    style = textStyle(
                        size = 14.sp,
                        weight = FontWeight.Bold
                    ),
                    color = AppColors.Black
                )
                Spacer(modifier = Modifier.weight(1f))
                DraftButton(onDraftClicked = {})
                Spacer(modifier = Modifier.width(8.dp))
                NewButton(onNewClicked = { viewModel.clearError(); showAddModal = true })
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
                        placeholder = "Search Inspections",
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
                        isFromIncident = true, // Reusing logic
                        moduleName = "Inspections",
                        onApply = { state -> 
                            viewModel.applyFilters(state)
                            showFilterModal = false
                        },
                        onDismiss = { showFilterModal = false }
                    )
                }

                selectedInspectionId?.let { id ->
                    val detailSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                    ModalBottomSheet(
                        onDismissRequest = { selectedInspectionId = null },
                        sheetState = detailSheetState,
                        containerColor = Color.White,
                        dragHandle = null
                    ) {
                        Box(modifier = Modifier.fillMaxHeight(0.9f)) {
                            org.example.project.ui.screens.InspectionDetailScreen(
                                inspectionId = id,
                                onBackClicked = { selectedInspectionId = null }
                            )
                        }
                    }
                }

                if (uiState.isLoading && uiState.inspections.isEmpty()) {
                    AppLoader()
                } else if (uiState.error != null && uiState.inspections.isEmpty()) {
                    ErrorRetryView(
                        errorMessage = uiState.error ?: "",
                        onRetryClick = { viewModel.fetchInspections(isRefresh = true) }
                    )
                } else {
                    PullToRefreshBox(
                        isRefreshing = uiState.isLoading,
                        onRefresh = { viewModel.fetchInspections(isRefresh = true) },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (uiState.inspections.isEmpty()) {
                            EmptyScreenView(
                                message = "No inspections found"
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize().padding(top = 25.dp)
                            ) {
                                items(uiState.inspections.size) { index ->
                                    if (index >= uiState.inspections.size - 1 && !uiState.isLoading && !uiState.isPaginating && !uiState.endReached) {
                                        LaunchedEffect(key1 = index) {
                                            viewModel.fetchInspections(isRefresh = false)
                                        }
                                    }
                                    InspectionListItem(
                                        inspection = uiState.inspections[index],
                                        onClick = { uiState.inspections[index].id?.let { selectedInspectionId = it } }
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
                if (showAddModal) {
                    ModalBottomSheet(
                        onDismissRequest = { showAddModal = false },
                        sheetState = sheetState,
                        containerColor = Color.White,
                        dragHandle = null
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 22.dp, vertical = 24.dp)
                                .navigationBarsPadding()
                        ) {
                            // Drag handle placeholder
                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .width(40.dp)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(Color(0xFFE0E0E0))
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            Text(
                                text = "Inspection Type",
                                style = textStyle(size = 18.sp, weight = FontWeight.Bold),
                                color = AppColors.Black
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            
                            if (uiState.isAuditItemsLoading) {
                                Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = AppColors.Primary)
                                }
                            } else if (uiState.auditItemsError != null) {
                                ErrorRetryView(errorMessage = uiState.auditItemsError ?: "", onRetryClick = {})
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    items(uiState.auditItems.size) { index ->
                                        val item = uiState.auditItems[index]
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { 
                                                    showAddModal = false 
                                                    onCreateClicked(item.auditItemId, item.auditItemTitle ?: "")
                                                }
                                                .padding(vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {

                                                WebImageView(
                                                    imageUrl = item.image,
                                                    modifier = Modifier.size(48.dp)
                                                )

                                            Spacer(modifier = Modifier.width(16.dp))
                                            Text(
                                                text = item.auditItemTitle ?: "",
                                                style = textStyle(size = 15.sp, weight = FontWeight.Bold),
                                                color = AppColors.Black,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Image(
                                                painter = painterResource(Res.drawable.ic_right_icon),
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp),
                                                colorFilter = ColorFilter.tint(Color.Gray)
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
                message = uiState.errorExcel ?: "",
                onDismiss = { viewModel.clearErrorExcel() },
                type = org.example.project.utilites.ToastType.Error,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 20.dp)
            )

            val exportToastMessage by viewModel.exportToastMessage.collectAsState()
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
fun InspectionListItem(
    inspection: InspectionData,
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
                    imageUrl = inspection.images?.firstOrNull()?.image ?: "",
                    modifier = Modifier
                        .width(70.dp)
                        .height(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                
                val totalImages = inspection.images?.size ?: 0
                if (totalImages > 1) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(4.dp)
                            .size(22.dp)
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+${totalImages - 1}",
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
                Text(
                    text = inspection.auditItem?.auditItemTitle ?: "-",
                    style = textStyle(size = 15.sp, weight = FontWeight.Bold),
                    color = AppColors.Black,
                    maxLines = 2
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
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
                                inspection.inspectionDate ?: "",
                                inputPattern = "dd-MM-yyyy",
                                outputPattern = "dd MMM yyyy"
                            ),
                            style = textStyle(size = 11.sp, weight = FontWeight.SemiBold),
                            color = AppColors.Black
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = timeAgo(
                            inspection.createdAt ?: "",
                            inputPattern = "yyyy-MM-dd HH:mm:ss",
                        ),
                        style = textStyle(size = 11.sp, weight = FontWeight.Normal),
                        color = Color.DarkGray
                    )
                }


                // User row
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WebImageView(
                        imageUrl = inspection.facilities?.groupImage ?: "",
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = inspection.facilities?.groupName ?: "",
                        style = textStyle(size = 11.sp, weight = FontWeight.Medium),
                        color = AppColors.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = inspection.facilities?.groupCode ?: "",
                        style = textStyle(size = 11.sp, weight = FontWeight.Medium),
                        color = AppColors.Black
                    )
                }
            }
        }
        HorizontalDivider()
    }
}
