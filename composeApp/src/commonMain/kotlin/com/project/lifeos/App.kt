package com.project.lifeos

import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.FadeTransition
import co.touchlab.kermit.Logger
import com.project.lifeos.di.AppModule
import com.project.lifeos.ui.bottomNavigation.BottomBar
import com.project.lifeos.ui.screen.AddTaskScreen

import org.jetbrains.compose.resources.ExperimentalResourceApi
import com.project.lifeos.ui.screen.HomeScreen

private const val TAG = "AppCommon"
private val logger = Logger.withTag(TAG)

@Composable
fun App(appModule: AppModule) {
    logger.d("Application started")
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Navigator(HomeScreen(appModule)) { navigator ->
                Scaffold(bottomBar = {
                    val activeScreen = mutableStateOf(
                        if (navigator.lastItem is HomeScreen) 0 else 1
                    )
                    logger.d("zxc $activeScreen ")
                    BottomBar(navigator, activeScreen, appModule)
                })
                {
                    FadeTransition(navigator)
                }
            }
        }
    }
}
