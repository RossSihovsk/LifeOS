package com.project.lifeos.ui.screen

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import co.touchlab.kermit.Logger
import com.project.lifeos.viewmodel.TaskViewModel


@Composable
expect fun AddTaskScreenContent(taskViewModel: TaskViewModel, logger: Logger)

private const val TAG = "AddTaskScreen"
private val logger = Logger.withTag(TAG)

class AddTaskScreen : Screen {
    @Composable
    override fun Content() {
        val taskViewModel = rememberScreenModel { TaskViewModel() }
        AddTaskScreenContent(taskViewModel, logger)
    }
}