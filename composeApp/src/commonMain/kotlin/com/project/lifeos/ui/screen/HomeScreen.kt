package com.project.lifeos.ui.screen

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.project.lifeos.di.AppModule
import com.project.lifeos.viewmodel.TaskViewModel

@Composable
expect fun HomeScreenContent(viewModel: TaskViewModel, navigator: Navigator? = null)

class HomeScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val taskViewModel = rememberScreenModel { AppModule.taskViewModel }
        HomeScreenContent(viewModel = taskViewModel, navigator = navigator)
    }
}