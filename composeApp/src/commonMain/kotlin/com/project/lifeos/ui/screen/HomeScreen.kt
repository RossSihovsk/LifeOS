package com.project.lifeos.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.project.lifeos.interactor.TestInteractor
import com.project.lifeos.repository.TestRepository
import com.project.lifeos.viewmodel.HomeViewModel

@Composable
expect fun HomeScreenContent(viewModel: HomeViewModel, navigator: Navigator)

class HomeScreen : Screen {
    @Composable
    override fun Content() {
        val repository = remember { TestRepository() }
        val interactor = remember { TestInteractor(repository) }
        val viewModel = remember { HomeViewModel(interactor) }
        val navigator = LocalNavigator.currentOrThrow
        HomeScreenContent(viewModel = viewModel, navigator = navigator)
    }
}