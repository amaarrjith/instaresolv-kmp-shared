package org.example.project.ui

import TabBarItems
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.R
import org.example.project.colors.AppColors
import org.example.project.typography.textStyle

@Composable
fun AppTabBar() {

    val selectedIndex = remember { mutableStateOf(0) }

    val navItems = listOf(
        TabBarItems(
            stringResource(R.string.home),
            R.drawable.ic_home_selected,
            R.drawable.ic_home_unselected
        ),
        TabBarItems(
            stringResource(R.string.project),
            R.drawable.ic_project_selected,
            R.drawable.ic_project_unselected
        ),
        TabBarItems(
            stringResource(R.string.briefs),
            R.drawable.ic_briefs_selected,
            R.drawable.ic_briefs_unselected
        ),
        TabBarItems(
            stringResource(R.string.settings),
            R.drawable.ic_settings_selected,
            R.drawable.ic_settings_unselected
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
                0 -> HomeScreenContentView()
                1 -> Text("Project Screen")
                2 -> Text("Briefs Screen")
                3 -> Text("Settings Screen")
            }
        }
    }
}

@Preview
@Composable
fun AppTabBarPreview() {
    AppTabBar()
}