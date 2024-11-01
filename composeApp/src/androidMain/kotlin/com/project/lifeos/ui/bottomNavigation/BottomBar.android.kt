package com.project.lifeos.ui.bottomNavigation

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import co.touchlab.kermit.Logger
import com.project.lifeos.di.AppModule
import com.project.lifeos.ui.screen.GoalScreen
import com.project.lifeos.ui.screen.HomeScreen
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
    val viewModel = appModule.addTaskViewModel
    logger.i("Navigate $bottomBarItems")

    when (bottomBarItems) {
        BottomBarItems.HOME -> {
            navigator.safePush(HomeScreen(appModule))
        }

        BottomBarItems.GOALS -> {
            navigator.safePush(GoalScreen(appModule))
        }

        BottomBarItems.ADD_TASK -> AddTaskBottomSheetView(viewModel, onDoneOrDismiss)
        BottomBarItems.STATS -> {}
        BottomBarItems.PROFILE -> {}
    }
}