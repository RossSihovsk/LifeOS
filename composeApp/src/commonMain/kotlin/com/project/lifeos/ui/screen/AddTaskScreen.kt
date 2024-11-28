package com.project.lifeos.ui.screen

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import co.touchlab.kermit.Logger
import com.project.lifeos.data.Priority
import com.project.lifeos.data.Reminder
import com.project.lifeos.data.TaskStatus
import com.project.lifeos.di.AppModuleProvider
import com.project.lifeos.viewmodel.AddTaskViewModel

@Composable
expect fun AddTaskScreenContent(viewModel: AddTaskViewModel?, logger: Logger?,onDone: (
    title: String,
    description: String?,
    time: Long?,
    dates: List<String>,
    checkItems: List<String>,
    status: TaskStatus,
    reminder: Reminder,
    priority: Priority,
) -> Unit)

private const val TAG = "AddTaskScreen"
private val logger = Logger.withTag(TAG)

class AddTaskScreen : Screen {
    @Composable
    override fun Content() {
        val taskViewModel = rememberScreenModel { AppModuleProvider.getAppModule().addTaskViewModel }
        AddTaskScreenContent(taskViewModel, logger){title, description, time, dates, checkItems, status, reminder, priority ->  }
    }
}