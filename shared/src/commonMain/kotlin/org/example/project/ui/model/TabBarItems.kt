package org.example.project.model

import org.jetbrains.compose.resources.DrawableResource

data class TabBarItems(
    val title: String,
    val selectedIcon: DrawableResource,
    val unselectedIcon: DrawableResource
)
