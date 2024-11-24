package com.project.lifeos.ui.screen

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import co.touchlab.kermit.Logger

@Composable
expect fun GoalScreenContent(navigator: Navigator)

private const val TAG = "GoalScreen"
private val logger = Logger.withTag(TAG)

class GoalScreen : Screen {
    @Composable
    override fun Content() {
//        val viewModel = rememberScreenModel { appModule.addTaskViewModel }
        val navigator = LocalNavigator.currentOrThrow
        GoalScreenContent(navigator)
    }
}