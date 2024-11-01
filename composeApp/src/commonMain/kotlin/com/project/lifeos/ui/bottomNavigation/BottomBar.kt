package com.project.lifeos.ui.bottomNavigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.navigator.Navigator
import co.touchlab.kermit.Logger
import com.project.lifeos.di.AppModule

private val logger = Logger.withTag("bottomBarNavigationCommon")

@Composable
expect fun bottomBarNavigation(
    bottomBarItems: BottomBarItems,
    navigator: Navigator,
    appModule: AppModule,
    onDoneOrDismiss: () -> Unit
)

private val items = BottomBarItems.entries.toList()

@Composable
fun BottomBar(navigator: Navigator, appModule: AppModule) {
    var previousItem by remember {  mutableStateOf(BottomBarItems.HOME) }
    var selectedItem by remember { mutableStateOf(BottomBarItems.HOME) }

    logger.d("BottomBar previousItem: $previousItem selectedItem:$selectedItem")

    bottomBarNavigation(selectedItem, navigator, appModule) {
        logger.d("DoneOrDismiss called previousItem: $previousItem selectedItem:$selectedItem")
        selectedItem = previousItem
        previousItem = BottomBarItems.HOME
    }

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(selected = item == selectedItem,
                onClick = {
                    logger.d("onClick item: $item selectedItem:$selectedItem")
                    if (item != selectedItem) {
                        previousItem = selectedItem
                        selectedItem = item
                    }
                }, icon = {
                    Icon(
                        imageVector = if (item == selectedItem) item.selectedIcon else item.unSelectedIcon,
                        contentDescription = item.title
                    )
                })
        }
    }
}