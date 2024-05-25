package com.project.lifeos.ui.bottomNavigation

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.project.lifeos.di.AppModule
import com.project.lifeos.ui.screen.addTask.AddTaskBottomSheetView

@Composable
actual fun bottomBarNavigation(screenName: String, navigator: Navigator, appModule: AppModule) {
    val viewModel = appModule.provideAddTaskViewModel()

    if (screenName == "Add Task") AddTaskBottomSheetView(viewModel)
}