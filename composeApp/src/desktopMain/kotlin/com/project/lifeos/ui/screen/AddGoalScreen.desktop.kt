package com.project.lifeos.ui.screen

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.project.lifeos.data.Category
import com.project.lifeos.data.Duration
import com.project.lifeos.data.Goal
import com.project.lifeos.data.Task
import com.project.lifeos.viewmodel.CreateGoalScreenViewModel

@Composable
actual fun AddGoalScreenContent(
    navigator: Navigator,
    viewModel: CreateGoalScreenViewModel?,
    goal: Goal?,
    onDone: (title: String, description: String, duration: Duration, category: Category, tasks: List<Task>) -> Unit
) {
}