package org.example.project.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_calendar
import instaresolv.shared.generated.resources.ic_filter
import org.example.project.colors.AppColors
import org.example.project.data.model.PendingActionItem
import org.example.project.data.model.PendingActionStatusType
import org.example.project.data.settings.timeAgo
import org.example.project.typography.textStyle
import org.example.project.ui.components.AppLoader
import org.example.project.utilites.ErrorRetryView
import org.example.project.utilites.NavigationBackIcon
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PendingActionListScreen(
    onBackClicked: () -> Unit
) {
    val viewModel: PendingActionListViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()
    
    var selectedTab by remember { mutableStateOf(0) }
    var showFilterSheet by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf<Int?>(null) }
    
    val sheetState = rememberModalBottomSheetState()
    
    Scaffold(
        containerColor = Color.White,
        topBar = {
            Row (
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavigationBackIcon(
                    onBackClicked
                )
                Text(
                    text = "Pending Actions".uppercase(),
                    style = textStyle(
                        size = 14.sp,
                        weight = FontWeight.Bold
                    ),
                    color = AppColors.Black
                )
                Spacer(modifier = Modifier.weight(1f))
                Image(
                    painter = painterResource(Res.drawable.ic_filter),
                    contentDescription = null,
                    modifier = Modifier.padding(end = 20.dp).clickable {
                        if (selectedTab == 0) {
                            showFilterSheet = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(paddingValues)
                .imePadding()
                .background(Color.White)
        ) {
            // Tabs Row
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                TabItem(
                    title = "General Actions",
                    isSelected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                Spacer(modifier = Modifier.width(24.dp))
                TabItem(
                    title = "Permit Actions",
                    isSelected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
            }
            
            // Content
            Box(modifier = Modifier.fillMaxSize().padding(horizontal = 22.dp)) {
                if (selectedTab == 0) {
                    // General Actions
                    if (uiState.isLoading) {
                        AppLoader()
                    } else if (uiState.error != null) {
                        ErrorRetryView(
                            errorMessage = uiState.error ?: "",
                            onRetryClick = { viewModel.fetchPendingActions() }
                        )
                    } else {
                        val filteredActions = if (selectedFilter == null) {
                            uiState.pendingActions
                        } else {
                            uiState.pendingActions.filter { it.type == selectedFilter }
                        }
                        
                        if (filteredActions.isEmpty()) {
                        Text(
                            text = "No pending actions found.",
                            color = AppColors.TextGray,
                            modifier = Modifier.align(Alignment.Center)
                        )
                        } else {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(filteredActions) { action ->
                                    PendingActionListItem(action)
                                }
                            }
                        }
                    }
                } else {
                    // Permit Actions
                    Text(
                        text = "Permit actions coming soon.",
                        color = AppColors.TextGray,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
    
    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = sheetState,
            containerColor = Color.White,
            dragHandle = null
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Filter by",
                        style = textStyle(size = 18.sp, weight = FontWeight.Bold),
                        color = AppColors.Black
                    )
                    if (selectedFilter != null) {
                        Text(
                            text = "Clear Filter",
                            style = textStyle(size = 14.sp, weight = FontWeight.Medium),
                            color = AppColors.Primary,
                            modifier = Modifier.clickable {
                                selectedFilter = null
                                showFilterSheet = false
                            }.padding(8.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                
                val filters = listOf(
                    PendingActionStatusType.OPEN_OBSERVATION to "Open Observation",
                    PendingActionStatusType.REQUEST_TO_JOIN_GROUP to "Request to Join Group",
                    PendingActionStatusType.OBSERVATION_RESPONSIBILITY_CHANGE to "Observation Responsibility Change",
                    PendingActionStatusType.REQUEST_TO_DELETE_OBSERVATION to "Request to Delete Observation",
                    PendingActionStatusType.REVIEW_OBSERVATION_CLOSEOUT to "Review Observation Closeout"
                )
                
                filters.forEach { (type, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedFilter = type
                                showFilterSheet = false
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedFilter == type,
                            onClick = {
                                selectedFilter = type
                                showFilterSheet = false
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = AppColors.Primary,
                                unselectedColor = AppColors.TextGray
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = label,
                            style = textStyle(
                                size = 16.sp,
                                weight = if (selectedFilter == type) FontWeight.SemiBold else FontWeight.Medium
                            ),
                            color = if (selectedFilter == type) AppColors.Primary else AppColors.Black
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun TabItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.clickable { onClick() }.padding(bottom = 4.dp).width(IntrinsicSize.Max)
    ) {
        Text(
            text = title,
            style = textStyle(
                size = 16.sp,
                weight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
            ),
            color = if (isSelected) AppColors.Primary else AppColors.TextGray
        )
        if (isSelected) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(AppColors.Primary)
            )
        }
    }
}

@Composable
fun PendingActionListItem(action: PendingActionItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        if (action.groupCode != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Project: ${action.groupCode}",
                    style = textStyle(size = 12.sp, weight = FontWeight.SemiBold),
                    color = AppColors.Primary
                )
                Spacer(modifier = Modifier.weight(1f))
                StatusBadge(type = action.type, isEditable = false)
            }
            Spacer(modifier = Modifier.height(4.dp))
        } else {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                StatusBadge(type = action.type, isEditable = false)
            }
        }
        if (action.description != null) {
            Text(
                text = action.description,
                style = textStyle(size = 14.sp, weight = FontWeight.Medium),
                color = AppColors.Black
            )
        }
        if (action.justification != null) {
            Text(
                text = action.justification,
                style = textStyle(size = 12.sp, weight = FontWeight.Normal),
                color = AppColors.Black
            )
        }
        if (action.date != null && action.date.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(Res.drawable.ic_calendar),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = org.example.project.data.settings.formatDate(action.date, "", "dd MMM yyyy"),
                    style = textStyle(size = 12.sp, weight = FontWeight.Normal),
                    color = AppColors.TextGray
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = timeAgo(action.date),
                    style = textStyle(size = 12.sp, weight = FontWeight.Normal),
                    color = AppColors.TextGray
                )
            }
        }
        if (action.justification != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Justification: ${action.justification}",
                style = textStyle(size = 12.sp, weight = FontWeight.Normal),
                color = AppColors.TextGray
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFE0E0E0))
        )
    }
}

@Composable
fun StatusBadge(type: Int, isEditable: Boolean) {
    val (text, bgColor, textColor) = when (type) {
        PendingActionStatusType.OPEN_OBSERVATION ->
            Triple("Open Observation", Color(0xFFE6FAF8), Color(0xFF28D29F))
        PendingActionStatusType.REQUEST_TO_JOIN_GROUP -> 
            Triple("Request to Join Project", Color(0xFFE4F7FF), Color(0xFF00B7FF))
        PendingActionStatusType.OBSERVATION_RESPONSIBILITY_CHANGE -> 
            Triple("Observation Responsibility Change", Color(0xFFFFEBE1), Color(0xFFFF846B))
        PendingActionStatusType.REQUEST_TO_DELETE_OBSERVATION -> 
            Triple("Request to delete observation", Color(0xFFF5F6FF), Color(0xFF536DFF))
        PendingActionStatusType.REVIEW_OBSERVATION_CLOSEOUT -> {
            if (isEditable) Triple("Review Observation Close Out", Color(0xFFFDF0D8), Color(0xFFF7B231))
            else Triple("Open Observation", Color(0xFFE6FAF8), Color(0xFF28D29F))
        }
        else -> Triple("", Color.Transparent, Color.Transparent)
    }

    if (text.isEmpty()) return

    Box(
        modifier = Modifier
            .background(color = bgColor, shape = RoundedCornerShape(3.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            style = textStyle(
                weight = FontWeight.Normal,
                size = 12.sp,
            ),
            color = textColor
        )
    }
}