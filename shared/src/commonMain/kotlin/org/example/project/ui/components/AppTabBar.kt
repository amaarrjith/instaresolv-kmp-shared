package org.example.project.ui

import org.example.project.model.TabBarItems
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.DpOffset
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.home
import instaresolv.shared.generated.resources.project
import instaresolv.shared.generated.resources.briefs
import instaresolv.shared.generated.resources.settings
import instaresolv.shared.generated.resources.ic_home_selected
import instaresolv.shared.generated.resources.ic_home_unselected
import instaresolv.shared.generated.resources.ic_project_selected
import instaresolv.shared.generated.resources.ic_project_unselected
import instaresolv.shared.generated.resources.ic_briefs_selected
import instaresolv.shared.generated.resources.ic_briefs_unselected
import instaresolv.shared.generated.resources.ic_settings_selected
import instaresolv.shared.generated.resources.ic_settings_unselected
import org.example.project.colors.AppColors
import org.example.project.tabbar.AppTabBarUiState
import org.example.project.tabbar.AppTabBarViewModel
import org.example.project.typography.textStyle
import org.example.project.ui.components.AppLoader
import org.example.project.ui.screens.BriefsScreen
import org.example.project.ui.screens.SettingsScreen
import org.koin.compose.koinInject

import androidx.compose.runtime.saveable.rememberSaveable
import org.example.project.data.model.Project

@Composable
fun AppTabBar(
    onProfileClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onModuleClicked: (ActionOverview) -> Unit,
    onCreateProjectClicked: () -> Unit,
    onProjectClicked: (Project) -> Unit
) {

    val selectedIndex = rememberSaveable { mutableStateOf(0) }
    val viewModel: AppTabBarViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    val navItems = listOf(
        TabBarItems(
            stringResource(Res.string.home),
            Res.drawable.ic_home_selected,
            Res.drawable.ic_home_unselected
        ),
        TabBarItems(
            stringResource(Res.string.project),
            Res.drawable.ic_project_selected,
            Res.drawable.ic_project_unselected
        ),
        TabBarItems(
            stringResource(Res.string.briefs),
            Res.drawable.ic_briefs_selected,
            Res.drawable.ic_briefs_unselected
        ),
        TabBarItems(
            stringResource(Res.string.settings),
            Res.drawable.ic_settings_selected,
            Res.drawable.ic_settings_unselected
        )
    )

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            Box(
                modifier = Modifier
                    .dropShadow(
                        shape = RectangleShape,
                        shadow = Shadow(
                            radius = 26.dp,
                            color = Color(0x0F000000),
                            offset = DpOffset(0.dp, (-4).dp)
                        )
                    )
                    .background(Color.White)
                    .padding(horizontal = 25.dp)
                    .padding(top = 10.dp)
            ) {
            NavigationBar(
                modifier = Modifier
                    .height(125.dp),
                containerColor = Color.White,
            ) {
                navItems.forEachIndexed { index, item ->

                    val isSelected = selectedIndex.value == index

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            selectedIndex.value = index
                        },
                        icon = {
                            Image(
                                painter = painterResource(
                                    if (isSelected) {
                                        item.selectedIcon
                                    } else {
                                        item.unselectedIcon
                                    }
                                ),
                                contentDescription = item.title
                            )
                        },
                        label = {
                            Text(
                                text = item.title.uppercase(),
                                style = textStyle(
                                    size = 12.sp,
                                    weight = FontWeight.SemiBold,
                                ),
                                color = if (isSelected) {
                                    AppColors.Primary
                                } else {
                                    AppColors.TextGray
                                }
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent,
                            selectedIconColor = Color.Transparent,
                            selectedTextColor = AppColors.Primary,
                            unselectedIconColor = Color.Transparent,
                            unselectedTextColor = AppColors.TextGray
                        )
                    )
                }
            }
        }
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is AppTabBarUiState.Success -> {
                    when (selectedIndex.value) {
                        0 -> HomeScreenContentView(
                            pullDownRefresh = { viewModel.getHomeContents(true) },
                            silentRefresh = { viewModel.getHomeContents(false) },
                            actionOverview = (viewModel.uiState.value as AppTabBarUiState.Success).actionsOverview,
                            assignedToMe = (viewModel.uiState.value as AppTabBarUiState.Success).assignedToMe,
                            onProfileClick = onProfileClick,
                            onNotificationClick = onNotificationClick,
                            isRefreshing = isRefreshing,
                            onClickModule = { module -> onModuleClicked(module) }
                        )
                        1 -> ProjectListScreen(
                            onCreateProjectClicked,
                            onProjectClicked = { project ->
                                onProjectClicked(project)
                            }
                        )
                        2 -> BriefsScreen(
                            actionsOverview = (viewModel.uiState.value as AppTabBarUiState.Success).actionsOverview,
//
                        )
                        3 -> SettingsScreen()
                    }
                }
                is AppTabBarUiState.Error -> {
                    when (selectedIndex.value) {
                        0 -> HomeScreenContentView(
                            pullDownRefresh = { viewModel.getHomeContents(true) },
                            silentRefresh = { viewModel.getHomeContents(false) },
                            onProfileClick = onProfileClick,
                            onNotificationClick = onNotificationClick,
                            isRefreshing = isRefreshing,
                            onClickModule = { module -> onModuleClicked(module) }
                        )
                        1 -> ProjectListScreen(
                            onCreateProjectClicked,
                            onProjectClicked = { project ->
                                onProjectClicked(project)
                            }
                        )
                        2 -> BriefsScreen(
//                            pullDownRefresh = { viewModel.getHomeContents(true) },
//                            silentRefresh = { viewModel.getHomeContents(false) },
//                            isRefreshing = isRefreshing
                        )
                        3 -> SettingsScreen()
                    }
                } is AppTabBarUiState.Loading -> {
                    AppLoader()
                }
            }
        }
    }
}
