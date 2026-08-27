package org.example.project.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_add
import instaresolv.shared.generated.resources.ic_calendar
import instaresolv.shared.generated.resources.ic_category
import instaresolv.shared.generated.resources.ic_right_icon
import org.example.project.utilites.rtlScale
import org.example.project.colors.AppColors
import org.example.project.data.model.PermitItem
import org.example.project.data.model.PermitStatus
import org.example.project.data.settings.formatDate
import org.example.project.data.settings.timeAgo
import org.example.project.typography.textStyle
import org.example.project.ui.components.AppFilterBottomSheet
import org.example.project.ui.components.AppLoader
import org.example.project.ui.components.WebImageView
import org.example.project.ui.screens.EmptyScreenView
import org.example.project.utilites.AppSearchBar
import org.example.project.utilites.ErrorRetryView
import org.example.project.utilites.NavigationBackIcon
import org.example.project.utilites.ToastHost
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import kotlin.time.Clock
import org.jetbrains.compose.resources.stringResource
import instaresolv.shared.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermitToWorkListScreen(
    onBackClicked: () -> Unit,
    onCreateClicked: (typeId: Int, typeName: String) -> Unit = { _, _ -> },
    onItemClicked: (Int) -> Unit = {}
) {
    val viewModel: PermitToWorkListViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()
    var showFilterModal by remember { mutableStateOf(false) }
    var showAddModal by remember { mutableStateOf(false) }
    val fileDownloader = org.example.project.utilites.rememberFileDownloader()
    LaunchedEffect(uiState.exportDownloadUrl) {
        uiState.exportDownloadUrl?.let { url ->
            try {
                val fileName = "Permit_Report_${Clock.System.now().toEpochMilliseconds()}.xlsx"
                fileDownloader.downloadFile(url, fileName)
            } catch (e: Exception) {
                viewModel.showError(e.message ?: "Download failed")
            }
            viewModel.clearExportDownloadUrl()
        }
    }
    Scaffold(
        floatingActionButton = {
            if (uiState.error == null && !uiState.permits.isEmpty()) {
                org.example.project.ui.components.excel.CommonExcelButton(
                    isLoading = uiState.isExporting,
                    onClick = { viewModel.generateExcel() }
                )
            }
        },
        containerColor = Color.White,
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
                    text = stringResource(Res.string.permitToWork).uppercase(),
                    style = textStyle(
                        size = 14.sp,
                        weight = FontWeight.Bold
                    ),
                    color = AppColors.Black
                )
                Spacer(modifier = Modifier.weight(1f))
                DraftButton(onDraftClicked = {})
                Spacer(modifier = Modifier.width(8.dp))
                NewButton(onNewClicked = {
                    if (viewModel.logginedUser?.projectDesignation?.contains(2) == true) {
                        viewModel.fetchPermitTypes()
                        showAddModal = true
                    } else {
                        viewModel.showError("You don't have permission to create a new permit. Only Requestors can perform this action.")
                    }
                })
            }
        },
        bottomBar = {

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
                        placeholder = stringResource(Res.string.searchPermits),
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
                        isFromPermit = true,
                        moduleName = "Permits",
                        onApply = { state ->
                            viewModel.applyFilters(state)
                            showFilterModal = false
                        },
                        onDismiss = { showFilterModal = false }
                    )
                }

                if (showAddModal) {
                    val addSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                    ModalBottomSheet(
                        onDismissRequest = { showAddModal = false },
                        sheetState = addSheetState,
                        containerColor = Color.White,
                        dragHandle = null
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 22.dp, vertical = 24.dp)
                                .navigationBarsPadding()
                        ) {
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
                                text = stringResource(Res.string.permitToWorkType),
                                style = textStyle(size = 18.sp, weight = FontWeight.Bold),
                                color = AppColors.Black
                            )
                            Spacer(modifier = Modifier.height(20.dp))

                            if (uiState.isTypesLoading) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(150.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = AppColors.Primary)
                                }
                            } else if (uiState.typesError != null) {
                                ErrorRetryView(
                                    errorMessage = uiState.typesError ?: "",
                                    onRetryClick = { viewModel.fetchPermitTypes() }
                                )
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    items(uiState.permitTypesList.size) { index ->
                                        val item = uiState.permitTypesList[index]
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    showAddModal = false
                                                    onCreateClicked(item.permitTypeId, item.permitTypeTitle ?: "")
                                                }
                                                .padding(vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            WebImageView(
                                                imageUrl = item.image ?: "",
                                                modifier = Modifier.size(48.dp)
                                            )

                                            Spacer(modifier = Modifier.width(16.dp))
                                            Text(
                                                text = item.permitTypeTitle ?: "",
                                                style = textStyle(size = 15.sp, weight = FontWeight.Bold),
                                                color = AppColors.Black,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Image(
                                                painter = painterResource(Res.drawable.ic_right_icon),
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp).rtlScale(),
                                                colorFilter = ColorFilter.tint(Color.Gray)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (uiState.isLoading && uiState.permits.isEmpty()) {
                    AppLoader()
                } else if (uiState.error != null && uiState.permits.isEmpty()) {
                    ErrorRetryView(
                        errorMessage = uiState.error ?: "",
                        onRetryClick = { viewModel.fetchPermits(isRefresh = true) }
                    )
                } else {
                    PullToRefreshBox(
                        isRefreshing = uiState.isLoading,
                        onRefresh = { viewModel.fetchPermits(isRefresh = true) },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (uiState.permits.isEmpty()) {
                            EmptyScreenView(
                                message = stringResource(Res.string.noPermitsFound),
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize()
                                    .padding(top = 25.dp),
                            ) {
                                items(uiState.permits.size) { index ->
                                    if (index >= uiState.permits.size - 1 && !uiState.isLoading && !uiState.isPaginating && !uiState.endReached) {
                                        LaunchedEffect(key1 = index) {
                                            viewModel.fetchPermits(isRefresh = false)
                                        }
                                    }
                                    PermitListItem(
                                        permit = uiState.permits[index],
                                        onClick = { onItemClicked(uiState.permits[index].id) }
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
                visible = uiState.error != null,
                message = uiState.error  ?: "",
                onDismiss = { viewModel.clearError() },
                type = org.example.project.utilites.ToastType.Error,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 20.dp)
            )

            ToastHost(
                visible = uiState.errorMessage != null,
                message = uiState.errorMessage  ?: "",
                onDismiss = { viewModel.clearError() },
                type = org.example.project.utilites.ToastType.Error,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 20.dp)
            )

            ToastHost(
                visible = uiState.exportError != null,
                message = uiState.exportError ?: "",
                onDismiss = { viewModel.clearExportError() },
                type = org.example.project.utilites.ToastType.Error,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 20.dp)
            )

            ToastHost(
                visible = uiState.exportSuccessMessage != null,
                message = uiState.exportSuccessMessage ?: "",
                onDismiss = { viewModel.clearExportSuccess() },
                type = org.example.project.utilites.ToastType.Success,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 20.dp)
            )
        }
    }
}


@Composable
private fun PermitListItem(
    permit: PermitItem,
    onClick: () -> Unit = {}
) {
    Column(modifier = Modifier.clickable { onClick() }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box {
                WebImageView(
                    imageUrl = permit.facilities?.groupImage ?: "",
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
                // Top Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val status = PermitStatus.fromValue(permit.status)
                    val statusTitle = status?.title ?: "UNKNOWN"
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (status != null) Color(status.colorHex) else Color.Gray)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = statusTitle.uppercase(),
                            style = textStyle(size = 9.sp, weight = FontWeight.Bold),
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Date Row
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
                            text = permit.certificateDate.takeIf { !it.isNullOrBlank() }?.let {
                                formatDate(
                                    it,
                                    inputPattern = "yyyy-MM-dd",
                                    outputPattern = "dd MMM yyyy"
                                )
                            } ?: "",
                            style = textStyle(size = 11.sp, weight = FontWeight.SemiBold),
                            color = AppColors.Black
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = timeAgo(
                            permit.createdAt ?: "",
                            inputPattern = "yyyy-MM-dd HH:mm:ss",
                            isUtc = true
                        ), // Need to format real date to relative string if required, using placeholder for now
                        style = textStyle(size = 11.sp, weight = FontWeight.Normal),
                        color = Color.DarkGray
                    )
                }

                // Permit Code / Type
                Text(
                    text = "${permit.permitType?.title ?: "Permit"} - ${permit.permitCode ?: ""}",
                    style = textStyle(size = 15.sp, weight = FontWeight.Bold),
                    color = AppColors.Black,
                    maxLines = 2
                )

                // Facility/Group info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WebImageView(
                        imageUrl = permit.facilities?.groupImage ?: "",
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        Text(
                            text = permit.facilities?.groupName ?: "",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = textStyle(size = 11.sp, weight = FontWeight.Medium),
                            color = AppColors.Black
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = permit.facilities?.groupCode ?: "",
                        maxLines = 1,
                        style = textStyle(size = 9.sp, weight = FontWeight.Medium),
                        color = AppColors.Black
                    )
                }
            }
        }
        HorizontalDivider()
    }
}
