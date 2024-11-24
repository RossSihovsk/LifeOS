package com.project.lifeos.ui.screen

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import co.touchlab.kermit.Logger
import com.project.lifeos.di.AppModuleProvider
import com.project.lifeos.viewmodel.GoalScreenViewModel

@Composable
expect fun GoalScreenContent(navigator: Navigator, viewModel: GoalScreenViewModel)

private const val TAG = "GoalScreen"
private val logger = Logger.withTag(TAG)

class GoalScreen : Screen {
    @Composable
    override fun Content() {
        logger.d("Active")
        val taskViewModel = rememberScreenModel { AppModuleProvider.getAppModule().goalScreenViewModel }
        val navigator = LocalNavigator.currentOrThrow
        GoalScreenContent(navigator, taskViewModel)
    }
}