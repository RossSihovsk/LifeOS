package com.project.lifeos.ui.screen

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import co.touchlab.kermit.Logger
import com.project.lifeos.data.Priority
import com.project.lifeos.data.Reminder
import com.project.lifeos.data.Task
import com.project.lifeos.di.AppModuleProvider
import com.project.lifeos.viewmodel.AddTaskViewModel

@Composable
expect fun AddTaskScreenContent(navigator: Navigator?=null,
                                viewModel: AddTaskViewModel?, task: Task?, logger: Logger?, onDone: (
    title: String,
    description: String?,
    time: Long?,
    dates: List<String>,
    checkItems: List<String>,
    reminder: Reminder,
    priority: Priority,
) -> Unit)

private const val TAG = "AddTaskScreen"
private val logger = Logger.withTag(TAG)

data class AddTaskScreen(private val task:Task? = null, val onDone: (
    title: String,
    description: String?,
    time: Long?,
    dates: List<String>,
    checkItems: List<String>,
    reminder: Reminder,
    priority: Priority,
) -> Unit ={ _, _, _, _, _, _, _, -> }) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val taskViewModel = rememberScreenModel { AppModuleProvider.getAppModule().addTaskViewModel }
        AddTaskScreenContent(navigator,taskViewModel,task, logger,onDone)
    }
}