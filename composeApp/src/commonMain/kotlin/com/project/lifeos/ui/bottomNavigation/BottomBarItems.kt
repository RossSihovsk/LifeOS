package com.project.lifeos.ui.bottomNavigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class BottomBarItems(
    val title: String,
    val selectedIcon: ImageVector,
    val unSelectedIcon: ImageVector
) {
    HOME(
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unSelectedIcon = Icons.Outlined.Home
    ),
    GOALS(
        title = "Goals",
        selectedIcon = Icons.Filled.Assistant,
        unSelectedIcon = Icons.Outlined.Assistant
    ),
    ADD_TASK(
        title = "Add Task",
        selectedIcon = Icons.Filled.Add,
        unSelectedIcon = Icons.Outlined.Add
    ),
    STATS(
        title = "Stats",
        selectedIcon = Icons.Filled.AlignVerticalBottom,
        unSelectedIcon = Icons.Outlined.AlignVerticalBottom
    ),
    PROFILE(
        title = "Profile",
        selectedIcon = Icons.Filled.Person,
        unSelectedIcon = Icons.Outlined.Person
    )
}