package com.project.lifeos.ui.bottomNavigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Star
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
        selectedIcon = Icons.Filled.Favorite,
        unSelectedIcon = Icons.Outlined.Favorite
    ),
    ADD_TASK(
        title = "Add Task",
        selectedIcon = Icons.Filled.Add,
        unSelectedIcon = Icons.Outlined.Add
    ),
    STATS(
        title = "Stats",
        selectedIcon = Icons.Filled.Star,
        unSelectedIcon = Icons.Outlined.Star
    ),
    PROFILE(
        title = "Profile",
        selectedIcon = Icons.Filled.Person,
        unSelectedIcon = Icons.Outlined.Person
    )
}