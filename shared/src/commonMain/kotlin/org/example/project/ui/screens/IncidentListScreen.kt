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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import instaresolv.shared.generated.resources.ic_download
import org.example.project.colors.AppColors
import org.example.project.data.model.IncidentData
import org.example.project.data.settings.formatDate
import org.example.project.data.settings.timeAgo
import org.example.project.typography.textStyle
import org.example.project.ui.IncidentType
import org.example.project.ui.components.AppFilterBottomSheet
import org.example.project.ui.components.AppLoader
import org.example.project.ui.components.WebImageView
import org.example.project.utilites.AppSearchBar
import org.example.project.utilites.ErrorRetryView
import org.example.project.utilites.NavigationBackIcon
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentListScreen(
    onBackClicked: () -> Unit,
    onCreateClicked: () -> Unit
) {
    val viewModel: IncidentListViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()
    var showFilterModal by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.White,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO Handle Download */ },
                shape = CircleShape,
                containerColor = AppColors.Primary,
                contentColor = Color.White
            ) {
                Image(
                    painter = painterResource(Res.drawable.ic_download),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Color.White),
                    modifier = Modifier.padding(bottom = 5.dp)
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
                    text = "Incidents".uppercase(),
                    style = textStyle(
                        size = 14.sp,
                        weight = FontWeight.Bold
                    ),
                    color = AppColors.Black
                )
                Spacer(modifier = Modifier.weight(1f))
                DraftButton(onDraftClicked = {})
                Spacer(modifier = Modifier.width(8.dp))
                NewButton(onNewClicked = {onCreateClicked()})
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
                        placeholder = "Search Incidents",
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
                        isFromIncident = true,
                        moduleName = "Incidents",
                        onApply = { state -> 
                            viewModel.applyFilters(state)
                            showFilterModal = false
                        },
                        onDismiss = { showFilterModal = false }
                    )
                }

                if (uiState.isLoading && uiState.incidents.isEmpty()) {
                    AppLoader()
                } else if (uiState.error != null && uiState.incidents.isEmpty()) {
                    ErrorRetryView(
                        errorMessage = uiState.error ?: "",
                        onRetryClick = { viewModel.loadIncidents(isRefresh = true) }
                    )
                } else {
                    PullToRefreshBox(
                        isRefreshing = uiState.isLoading,
                        onRefresh = { viewModel.loadIncidents(isRefresh = true) },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (uiState.incidents.isEmpty()) {
                            EmptyScreenView(
                                message = "No incidents found"
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize().padding(top = 25.dp)
                            ) {
                                items(uiState.incidents.size) { index ->
                                    if (index >= uiState.incidents.size - 1 && !uiState.isLoading && !uiState.isPaginating && !uiState.isLastPage) {
                                        LaunchedEffect(key1 = index) {
                                            viewModel.loadIncidents(isRefresh = false)
                                        }
                                    }
                                    IncidentListItem(
                                        incident = uiState.incidents[index],
                                        onClick = { /* TODO Handle detail click */ }
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
        }
    }
}

@Composable
fun IncidentListItem(
    incident: IncidentData,
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
                    imageUrl = incident.images?.firstOrNull()?.image ?: "",
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
                // Title from IncidentType
                Text(
                    text = IncidentType.getTitlesFromIds(incident.incidentType),
                    style = textStyle(size = 15.sp, weight = FontWeight.Bold),
                    color = AppColors.Black,
                    maxLines = 3
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Time ago
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
                                incident.incidentDate ?: "",
                                inputPattern = "yyyy-MM-dd HH:mm:ss",
                                outputPattern = "dd MMM yyyy"
                            ),
                            style = textStyle(size = 11.sp, weight = FontWeight.SemiBold),
                            color = AppColors.Black
                        )
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    // Date
                    Text(
                        text = timeAgo(
                            incident.createdAt ?: "",
                            inputPattern = "yyyy-MM-dd HH:mm:ss"
                        ),
                        style = textStyle(size = 11.sp, weight = FontWeight.Normal),
                        color = Color.DarkGray
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))
                // User row
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WebImageView(
                        imageUrl = incident.facilities?.groupImage ?: "",
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = incident.facilities?.groupName ?: "",
                        style = textStyle(size = 11.sp, weight = FontWeight.Medium),
                        color = AppColors.Black
                    )
                }
            }
        }
        HorizontalDivider()
    }
}
