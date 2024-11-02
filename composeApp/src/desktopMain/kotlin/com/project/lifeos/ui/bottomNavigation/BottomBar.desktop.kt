package com.project.lifeos.ui.bottomNavigation

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.project.lifeos.di.AppModule
import com.project.lifeos.ui.screen.AddTaskScreen
import com.project.lifeos.ui.screen.GoalScreen
import com.project.lifeos.ui.screen.HomeScreen
import com.project.lifeos.utils.safePush


@Composable
actual fun bottomBarNavigation(    bottomBarItems: BottomBarItems,
                                   navigator: Navigator,
                                   appModule: AppModule,
                                   onDoneOrDismiss: () -> Unit) {

        val viewModel = appModule.addTaskViewModel

        when (bottomBarItems) {
            BottomBarItems.HOME -> {
                navigator.safePush(HomeScreen(appModule))
            }

            BottomBarItems.GOALS -> {
                navigator.safePush(GoalScreen(appModule))
            }

            BottomBarItems.ADD_TASK -> {navigator.safePush(AddTaskScreen(appModule))}
            BottomBarItems.STATS -> {}
            BottomBarItems.PROFILE -> {}
}}