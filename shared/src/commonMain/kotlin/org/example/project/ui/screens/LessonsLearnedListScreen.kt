package org.example.project.ui.screens

import org.example.project.ui.components.AppFilterBottomSheet
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.width
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
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
import org.example.project.data.model.LessonLearnedData
import org.example.project.ui.components.AppLoader
import org.example.project.utilites.ErrorRetryView
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_add
import instaresolv.shared.generated.resources.ic_filter
import instaresolv.shared.generated.resources.ic_category
import instaresolv.shared.generated.resources.ic_calendar
import org.example.project.colors.AppColors
import org.example.project.data.settings.timeAgo
import org.example.project.typography.textStyle
import org.example.project.ui.components.WebImageView
import org.example.project.utilites.AppPrimaryButton
import org.example.project.utilites.AppSearchBar
import org.example.project.utilites.NavigationBackIcon
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonsLearnedListScreen(
    onBackClicked: () -> Unit,
    onCreateClicked: () -> Unit
) {
    val viewModel: LessonsLearnedListViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()
    var showFilterModal by remember { mutableStateOf(false) }
    var selectedLessonId by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        modifier = Modifier.fillMaxSize().background(AppColors.White).statusBarsPadding().navigationBarsPadding(),
        containerColor = AppColors.White,
        topBar = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NavigationBackIcon(onBackClick = onBackClicked)
                    Text(
                        text = "Lessons Learned",
                        modifier = Modifier.weight(1f),
                        style = textStyle(18.sp, FontWeight.SemiBold),
                        color = AppColors.Black
                    )
                }
                HorizontalDivider(color = Color(0xFFF0F0F0))
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                AppPrimaryButton(
                    text = "CREATE LESSON LEARNED",
                    onClick = onCreateClicked,
                    icon = Res.drawable.ic_add,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppSearchBar(
                    modifier = Modifier.weight(1f),
                    query = uiState.searchKey,
                    onQueryChange = { viewModel.updateSearchKey(it) },
                    placeholder = "Search Lessons Learned"
                )

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            if (uiState.appliedFilterState.hasActiveFilters()) Color(0xFFF4F6FF) else Color(0xFFF8F8F8),
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { showFilterModal = true },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(Res.drawable.ic_filter),
                        contentDescription = "Filter",
                        colorFilter = ColorFilter.tint(if (uiState.appliedFilterState.hasActiveFilters()) AppColors.Primary else AppColors.TextGray),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                if (uiState.error != null && uiState.lessons.isEmpty()) {
                    ErrorRetryView(
                        message = uiState.error!!,
                        onRetry = { viewModel.fetchLessonsLearned(isRefresh = true) }
                    )
                } else if (uiState.isLoading && uiState.lessons.isEmpty()) {
                    AppLoader(modifier = Modifier.fillMaxSize())
                } else {
                    PullToRefreshBox(
                        isRefreshing = uiState.isLoading,
                        onRefresh = { viewModel.fetchLessonsLearned(isRefresh = true) }
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(uiState.lessons.size) { index ->
                                val item = uiState.lessons[index]
                                LessonLearnedItem(
                                    item = item,
                                    onClick = { selectedLessonId = item.id }
                                )
                                if (index < uiState.lessons.size - 1) {
                                    HorizontalDivider(
                                        color = Color(0xFFF0F0F0),
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                }
                            }
                            if (uiState.isPaginating) {
                                item {
                                    Box(
                                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(color = AppColors.Primary)
                                    }
                                }
                            } else if (!uiState.endReached) {
                                item {
                                    LaunchedEffect(Unit) {
                                        viewModel.fetchLessonsLearned(isRefresh = false)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showFilterModal) {
        // AppFilterBottomSheet( ... ) Wait, we just need basic sorting since we don't have project selection for now or we can use the default.
        // We will just pass the default filter bottom sheet used everywhere.
        AppFilterBottomSheet(
            showProjects = true,
            showReportedBy = true,
            initialState = uiState.appliedFilterState,
            onApply = { state ->
                viewModel.applyFilters(state)
                showFilterModal = false
            },
            onDismiss = { showFilterModal = false }
        )
    }

    selectedLessonId?.let { id ->
        ModalBottomSheet(
            onDismissRequest = { selectedLessonId = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Color.White,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            LessonsLearnedDetailScreen(
                id = id,
                onClose = { selectedLessonId = null }
            )
        }
    }
}

@Composable
fun LessonLearnedItem(
    item: LessonLearnedData,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            WebImageView(
                imageUrl = item.facilities?.groupImage ?: "",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title ?: "-",
                    style = textStyle(14.sp, FontWeight.SemiBold),
                    color = AppColors.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.facilities?.groupName ?: "-",
                        style = textStyle(12.sp, FontWeight.Normal),
                        color = AppColors.TextGray
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.size(4.dp).background(Color(0xFFD9D9D9), CircleShape))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = item.createdAt?.timeAgo() ?: "-",
                        style = textStyle(12.sp, FontWeight.Normal),
                        color = AppColors.TextGray
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(Res.drawable.ic_category),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    colorFilter = ColorFilter.tint(AppColors.TextGray)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = item.reportedBy ?: "-",
                    style = textStyle(12.sp, FontWeight.Medium),
                    color = AppColors.Black
                )
            }
            
            Text(
                text = item.createdAt?.take(10) ?: "-",
                style = textStyle(12.sp, FontWeight.Normal),
                color = AppColors.TextGray
            )
        }
    }
}
