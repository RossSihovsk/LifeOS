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
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.navigator.Navigator
import com.project.lifeos.di.AppModule

@Composable
expect fun bottomBarNavigation(screenName: String, navigator: Navigator, appModule: AppModule)

private val items = listOf(
    BottomNavigationItem(
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unSelectedIcon = Icons.Outlined.Home
    ),
    BottomNavigationItem(
        title = "AddTask",
        selectedIcon = Icons.Filled.Add,
        unSelectedIcon = Icons.Outlined.Add
    ),
)
@Composable
fun BottomBar(navigator: Navigator, selectedItemIndex: MutableState<Int>, appModule: AppModule) {
    var selectedItem by remember { mutableStateOf("Home")}
    bottomBarNavigation(selectedItem, navigator, appModule)

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(selected = selectedItemIndex.value == index,
                onClick = {
                    if (selectedItemIndex.value != index) {
                        selectedItemIndex.value = index
                        selectedItem = item.title
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