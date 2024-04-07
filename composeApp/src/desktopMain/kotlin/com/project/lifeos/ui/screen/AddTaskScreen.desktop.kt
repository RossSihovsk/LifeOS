package com.project.lifeos.ui.screen

import androidx.compose.runtime.Composable
import co.touchlab.kermit.Logger
import com.project.lifeos.viewmodel.CreateTaskScreenViewModel

@Composable
actual fun AddTaskScreenContent(viewModel: CreateTaskScreenViewModel, logger: Logger) {
    viewModel.saveTask(
        "test",
        date = 1712519461312
    )
}