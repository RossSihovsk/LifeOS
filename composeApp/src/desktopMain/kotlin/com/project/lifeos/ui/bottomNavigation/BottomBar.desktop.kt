package com.project.lifeos.ui.bottomNavigation

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.project.lifeos.di.AppModule
import com.project.lifeos.ui.screen.AddTaskScreen


@Composable
actual fun bottomBarNavigation(screenName: String, navigator: Navigator, appModule: AppModule) {
navigator.push(AddTaskScreen(appModule))
}