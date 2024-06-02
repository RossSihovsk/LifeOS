package com.project.lifeos.ui.bottomNavigation

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import co.touchlab.kermit.Logger
import com.project.lifeos.di.AppModule
import com.project.lifeos.ui.screen.AddTaskScreen
private val logger = Logger.withTag("BottomBarNavigation")

@Composable
actual fun bottomBarNavigation(screenName: String, navigator: Navigator, appModule: AppModule) {
    logger.i("excepted error $screenName")
    if (screenName == "AddTask") {
        navigator.popUntilRoot()

        navigator.replaceAll(AddTaskScreen(appModule))

    }

}

