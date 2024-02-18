package com.project.lifeos.ui.bottomNavigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.navigator.Navigator
import com.project.lifeos.ui.screen.AddTaskScreen
import com.project.lifeos.ui.screen.HomeScreen

private val items = listOf(
    BottomNavigationItem(
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unSelectedIcon = Icons.Outlined.Home
    ),
    BottomNavigationItem(
        title = "Add Task",
        selectedIcon = Icons.Filled.Add,
        unSelectedIcon = Icons.Outlined.Add
    ),
)

@Composable
fun BottomBar(navigator: Navigator, selectedItemIndex: MutableState<Int>) {
    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(selected = selectedItemIndex.value == index,
                onClick = {
                    if (selectedItemIndex.value != index) {
                        selectedItemIndex.value = index
                        if (item.title == "Home") {
                            navigator.replaceAll(HomeScreen())
                        } else navigator.replaceAll(AddTaskScreen())

                    }
                }, icon = {
                    Icon(
                        imageVector = if (index == selectedItemIndex.value) item.selectedIcon else item.unSelectedIcon,
                        contentDescription = item.title
                    )
                })
        }
    }
}