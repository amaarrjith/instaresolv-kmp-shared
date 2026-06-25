package org.example.project.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.rotate
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.ui.text.style.TextAlign
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_calendar
import instaresolv.shared.generated.resources.ic_filter
import instaresolv.shared.generated.resources.ic_arrow_left
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
    var selectedFilters by remember { mutableStateOf<Set<Int>>(emptySet()) }
    
    val sheetState = rememberModalBottomSheetState()
    var selectedActionForSheet by remember { mutableStateOf<PendingActionItem?>(null) }
    
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
                        val filteredActions = if (selectedFilters.isEmpty()) {
                            uiState.pendingActions
                        } else {
                            uiState.pendingActions.filter { selectedFilters.contains(it.type) }
                        }
                        
                        if (filteredActions.isEmpty()) {
                        Text(
                            text = "No pending actions found.",
                            color = AppColors.TextGray,
                            modifier = Modifier.align(Alignment.Center)
                        )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(15.dp)
                            ) {
                                items(filteredActions) { action ->
                                    PendingActionListItem(
                                        action = action,
                                        onClick = { selectedActionForSheet = action }
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Permit Actions
                    EmptyScreenView(
                        "No permit actions found."
                    )
                }
            }
        }
    }
    
    if (showFilterSheet) {
        var tempFilters by remember { mutableStateOf(selectedFilters) }
        
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = sheetState,
            containerColor = Color.White,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Filter by",
                    style = textStyle(size = 18.sp, weight = FontWeight.Bold),
                    color = AppColors.Black
                )
                Spacer(modifier = Modifier.height(20.dp))
                
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
                                tempFilters = if (tempFilters.contains(type)) tempFilters - type else tempFilters + type
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = tempFilters.contains(type),
                            onClick = {
                                tempFilters = if (tempFilters.contains(type)) tempFilters - type else tempFilters + type
                            },
                            modifier = Modifier.size(20.dp),
                            colors = RadioButtonDefaults.colors(
                                selectedColor = AppColors.Primary,
                                unselectedColor = AppColors.TextGray
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = label,
                            style = textStyle(
                                size = 14.sp,
                                weight = if (tempFilters.contains(type)) FontWeight.SemiBold else FontWeight.Normal
                            ),
                            color = if (tempFilters.contains(type)) AppColors.Primary else AppColors.Black
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(30.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f).clickable { showFilterSheet = false }.padding(15.dp), contentAlignment = Alignment.Center) {
                        Text(text = "Cancel", style = textStyle(size = 16.sp, weight = FontWeight.Medium), color = AppColors.TextGray)
                    }
                    Box(modifier = Modifier.width(1.dp).height(20.dp).background(AppColors.TextGray.copy(alpha = 0.3f)))
                    Box(modifier = Modifier.weight(1f).clickable { 
                        selectedFilters = tempFilters
                        showFilterSheet = false 
                    }.padding(15.dp), contentAlignment = Alignment.Center) {
                        Text(text = "Apply", style = textStyle(size = 16.sp, weight = FontWeight.Medium), color = AppColors.Primary)
                    }
                }
            }
        }
    }

    if (selectedActionForSheet != null) {
        val action = selectedActionForSheet!!
        ModalBottomSheet(
            onDismissRequest = { selectedActionForSheet = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Color.White,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                val title = when (action.type) {
                    PendingActionStatusType.OPEN_OBSERVATION -> "Open Observation"
                    PendingActionStatusType.REQUEST_TO_JOIN_GROUP -> "Request to Join Project"
                    PendingActionStatusType.OBSERVATION_RESPONSIBILITY_CHANGE -> "Observation Responsibility Change"
                    PendingActionStatusType.REQUEST_TO_DELETE_OBSERVATION -> "Request to delete observation"
                    PendingActionStatusType.REVIEW_OBSERVATION_CLOSEOUT -> "Review Observation Closeout"
                    else -> ""
                }

                Text(
                    text = title,
                    style = textStyle(size = 18.sp, weight = FontWeight.Bold),
                    color = AppColors.Black
                )
                Spacer(modifier = Modifier.height(20.dp))

                when (action.type) {
                    PendingActionStatusType.OPEN_OBSERVATION -> {
                        ActionRow(title = "View Report")
                        ActionRow(title = "Generate PDF")
                        ActionRow(title = "Close Observation")
                        ActionRow(title = "Request Observation Responsible\nPerson Change")
                        ActionRow(title = "Request to Delete Observation")
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                    PendingActionStatusType.REVIEW_OBSERVATION_CLOSEOUT -> {
                        ActionRow(title = "View Observation Closeout")
                        Spacer(modifier = Modifier.height(30.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.weight(1f).clickable { selectedActionForSheet = null }.padding(15.dp), contentAlignment = Alignment.Center) {
                                Text(text = "Reject", style = textStyle(size = 16.sp, weight = FontWeight.Medium), color = AppColors.TextGray)
                            }
                            Box(modifier = Modifier.width(1.dp).height(20.dp).background(AppColors.TextGray.copy(alpha = 0.3f)))
                            Box(modifier = Modifier.weight(1f).clickable { selectedActionForSheet = null }.padding(15.dp), contentAlignment = Alignment.Center) {
                                Text(text = "Approve", style = textStyle(size = 16.sp, weight = FontWeight.Medium), color = AppColors.Primary)
                            }
                        }
                    }
                    else -> {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
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
fun PendingActionListItem(
    action: PendingActionItem,
    isEditable: Boolean = false,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, AppColors.TextGray),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 20.dp)
        ) {

            // Top Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                StatusBadge(
                    type = action.type,
                    isEditable = isEditable
                )

                Spacer(modifier = Modifier.weight(1f))

                if (!action.date.isNullOrEmpty()) {
                    Text(
                        text = timeAgo(action.date),
                        style = textStyle(
                            size = 12.sp,
                            weight = FontWeight.Normal
                        ),
                        color = AppColors.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(13.dp))

            // Date + Group Code
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Image(
                    painter = painterResource(Res.drawable.ic_calendar),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Color.Red)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = if (!action.date.isNullOrEmpty())
                        org.example.project.data.settings.formatDate(
                            action.date,
                            "",
                            "dd MMM yyyy"
                        ) else "",
                    style = textStyle(
                        size = 12.sp,
                        weight = FontWeight.Normal
                    ),
                    color = AppColors.Black
                )

                if (!action.groupCode.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.width(18.dp))

                    Text(
                        text = action.groupCode,
                        style = textStyle(
                            size = 12.sp,
                            weight = FontWeight.Normal
                        ),
                        color = AppColors.Black
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(13.dp))

            HorizontalDivider(
                thickness = 1.dp,
                color = AppColors.TextGray
            )

            Spacer(modifier = Modifier.height(13.dp))

            // Description
            if (!action.description.isNullOrEmpty()) {
                Text(
                    text = action.description,
                    style = textStyle(
                        size = 12.sp,
                        weight = FontWeight.Normal
                    ),
                    color = AppColors.Black
                )
            }
        }
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
            Triple("Review Observation Close Out", Color(0xFFFDF0D8), Color(0xFFF7B231))
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

@Composable
fun ActionRow(title: String, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = textStyle(size = 14.sp, weight = FontWeight.Normal),
            color = AppColors.Black,
            textAlign = TextAlign.Start
        )
        Image(
            painter = painterResource(Res.drawable.ic_arrow_left),
            contentDescription = null,
            colorFilter = ColorFilter.tint(AppColors.Black),
            modifier = Modifier.rotate(180f).size(20.dp)
        )
    }
}