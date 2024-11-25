package com.project.lifeos.ui.bottomNavigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import com.project.lifeos.di.AppModule
import com.project.lifeos.ui.screen.AddTaskScreen
import com.project.lifeos.ui.screen.GoalScreen
import com.project.lifeos.ui.screen.HomeScreen
import com.project.lifeos.ui.screen.ProfileScreen
import com.project.lifeos.utils.safePush

@Composable
actual fun bottomBarNavigation(
    bottomBarItems: BottomBarItems,
    navigator: Navigator,
    appModule: AppModule,
    onDoneOrDismiss: () -> Unit
) {

    var activeScreen by remember {  mutableStateOf(BottomBarItems.HOME) }
    if (activeScreen == bottomBarItems) return

    when (bottomBarItems) {
        BottomBarItems.HOME -> {
            navigator.safePush(HomeScreen())
        }

        BottomBarItems.GOALS -> {
            navigator.safePush(GoalScreen())
        }

        BottomBarItems.ADD_TASK -> {
            navigator.safePush(AddTaskScreen())
        }

        BottomBarItems.STATS -> {}
        BottomBarItems.PROFILE -> {
            navigator.safePush(ProfileScreen())
        }
    }

    activeScreen = bottomBarItems
}