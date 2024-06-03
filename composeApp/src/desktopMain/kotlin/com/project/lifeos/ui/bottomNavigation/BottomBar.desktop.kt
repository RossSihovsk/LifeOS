package com.project.lifeos.ui.bottomNavigation

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import co.touchlab.kermit.Logger
import com.project.lifeos.di.AppModule
import com.project.lifeos.ui.screen.AddTaskScreen
import com.project.lifeos.ui.screen.HomeScreen

private val logger = Logger.withTag("BottomBarNavigation")

@Composable
actual fun bottomBarNavigation(screenName: String, navigator: Navigator, appModule: AppModule) {
    logger.i("Navigating to screen: $screenName")
    when (screenName) {
       // "Home" -> navigator.replaceAll(HomeScreen(appModule))
        "AddTask" -> navigator.replaceAll(AddTaskScreen(appModule))
        else -> logger.w("Unknown screen: $screenName")
    }
}