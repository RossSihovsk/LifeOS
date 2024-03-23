package com.project.lifeos.ui.screen

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import co.touchlab.kermit.Logger
import com.project.lifeos.di.AppModule
import com.project.lifeos.viewmodel.CreateTaskScreenViewModel


@Composable
expect fun AddTaskScreenContent(viewModel: CreateTaskScreenViewModel, logger: Logger)

private const val TAG = "AddTaskScreen"
private val logger = Logger.withTag(TAG)

class AddTaskScreen : Screen {
    @Composable
    override fun Content() {
        val taskViewModel = rememberScreenModel { AppModule.createTaskScreenViewModel }
        AddTaskScreenContent(taskViewModel, logger)
    }
}