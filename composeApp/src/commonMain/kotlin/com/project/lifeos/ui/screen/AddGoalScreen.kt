package com.project.lifeos.ui.screen

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import co.touchlab.kermit.Logger
import com.project.lifeos.di.AppModuleProvider
import com.project.lifeos.viewmodel.CreateGoalScreenViewModel

@Composable
expect fun AddGoalScreenContent(navigator: Navigator, viewModel: CreateGoalScreenViewModel)

private val logger = Logger.withTag("AddGoalScreen")

class AddGoalScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = rememberScreenModel { AppModuleProvider.getAppModule().createGoalScreenViewModel }
        val navigator = LocalNavigator.currentOrThrow
        logger.d("Active")
        AddGoalScreenContent(navigator, viewModel)
    }
}