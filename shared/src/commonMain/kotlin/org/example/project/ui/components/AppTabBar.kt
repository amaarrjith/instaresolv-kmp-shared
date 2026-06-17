package org.example.project.ui

import org.example.project.model.TabBarItems
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import org.example.project.typography.textStyle

@Composable
fun AppTabBar(
    onProfileClick: () -> Unit = {}
) {

    val selectedIndex = remember { mutableStateOf(0) }

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
        bottomBar = {
            Box(
                modifier = Modifier
                    .background(Color.White)
                    .padding(horizontal = 25.dp)
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
            when (selectedIndex.value) {
                0 -> HomeScreenContentView(
                    onProfileClick = onProfileClick
                )
                1 -> ProjectListScreen()
                2 -> Text("Briefs Screen")
                3 -> Text("Settings Screen")
            }
        }
    }
}
