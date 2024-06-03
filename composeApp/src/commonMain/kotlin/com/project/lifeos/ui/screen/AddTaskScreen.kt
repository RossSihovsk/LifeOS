package com.project.lifeos.ui.screen

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import co.touchlab.kermit.Logger
import com.project.lifeos.di.AppModule
import com.project.lifeos.viewmodel.AddTaskViewModel


@Composable
expect fun AddTaskScreenContent(viewModel: AddTaskViewModel, logger: Logger)

private const val TAG = "AddTaskScreen"
private val logger = Logger.withTag(TAG)


class AddTaskScreen(private val appModule: AppModule) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val taskViewModel = rememberScreenModel { appModule.provideAddTaskViewModel() }
        logger.i("AddTaskScreen active")
        AddTaskScreenContent(taskViewModel, logger)
    }
}