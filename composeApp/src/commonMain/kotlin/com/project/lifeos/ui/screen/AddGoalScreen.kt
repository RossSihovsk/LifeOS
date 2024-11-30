package com.project.lifeos.ui.screen

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import co.touchlab.kermit.Logger
import com.project.lifeos.data.Category
import com.project.lifeos.data.Duration
import com.project.lifeos.data.Goal
import com.project.lifeos.data.Task
import com.project.lifeos.di.AppModuleProvider
import com.project.lifeos.viewmodel.CreateGoalScreenViewModel

@Composable
expect fun AddGoalScreenContent(
    navigator: Navigator,
    viewModel: CreateGoalScreenViewModel?,
    goal: Goal?,
    onDone: (title: String, description: String, duration: Duration, category: Category, tasks: List<Task>) -> Unit
)

private val logger = Logger.withTag("AddGoalScreen")

data class AddGoalScreen(
    private val goal: Goal? = null,
    val onDone: (title: String, description: String, duration: Duration, category: Category, tasks: List<Task>) -> Unit = { _, _, _, _, _ -> }
) : Screen {
    @Composable
    override fun Content() {
        val viewModel = rememberScreenModel { AppModuleProvider.getAppModule().createGoalScreenViewModel }
        val navigator = LocalNavigator.currentOrThrow
        logger.d("Active")
        AddGoalScreenContent(navigator, viewModel, goal, onDone)
    }
}