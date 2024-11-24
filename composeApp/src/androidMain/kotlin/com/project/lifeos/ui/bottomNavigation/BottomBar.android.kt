package com.project.lifeos.ui.bottomNavigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.navigator.Navigator
import co.touchlab.kermit.Logger
import com.project.lifeos.data.Priority
import com.project.lifeos.data.Reminder
import com.project.lifeos.data.TaskStatus
import com.project.lifeos.di.AppModule
import com.project.lifeos.di.AppModuleProvider
import com.project.lifeos.ui.screen.GoalScreen
import com.project.lifeos.ui.screen.HomeScreen
import com.project.lifeos.ui.screen.ProfileScreen
import com.project.lifeos.ui.screen.addTask.AddTaskBottomSheetView
import com.project.lifeos.utils.safePush

private val logger = Logger.withTag("bottomBarNavigationAndroid")

@Composable
actual fun bottomBarNavigation(
    bottomBarItems: BottomBarItems,
    navigator: Navigator,
    appModule: AppModule,
    onDoneOrDismiss: () -> Unit
) {
    var activeScreen by remember { mutableStateOf(BottomBarItems.HOME) }
    if (activeScreen == bottomBarItems && bottomBarItems != BottomBarItems.ADD_TASK) return
    logger.i("Navigate $bottomBarItems")

    when (bottomBarItems) {
        BottomBarItems.HOME -> {
            navigator.safePush(HomeScreen())
        }

        BottomBarItems.GOALS -> {
            navigator.safePush(GoalScreen())
        }

        BottomBarItems.ADD_TASK -> {
            AddTaskBottomSheetView(
                appModule.addTaskViewModel,
                onDismiss = onDoneOrDismiss,
                onDone = { _, _, _, _, _, _, _, _ -> onDoneOrDismiss() })
        }

        BottomBarItems.STATS -> {}
        BottomBarItems.PROFILE -> {
            navigator.safePush(ProfileScreen())
        }
    }

    activeScreen = bottomBarItems
}