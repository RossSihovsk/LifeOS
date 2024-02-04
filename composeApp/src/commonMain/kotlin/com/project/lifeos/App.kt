package com.project.lifeos

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import com.project.lifeos.interactor.TestInteractor
import com.project.lifeos.repository.TestRepository
import org.jetbrains.compose.resources.ExperimentalResourceApi
import com.project.lifeos.ui.HomeScreen
import com.project.lifeos.viewmodel.HomeViewModel

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {
    MaterialTheme {
        val repository = TestRepository()
        val interactor = TestInteractor(repository)
        val viewModel = HomeViewModel(interactor)
        HomeScreen(viewModel)
    }
}

