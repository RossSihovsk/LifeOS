package com.project.lifeos.ui.screen

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
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
        val taskViewModel = rememberScreenModel { appModule.addTaskViewModel }
        AddTaskScreenContent(taskViewModel, logger)
    }
}