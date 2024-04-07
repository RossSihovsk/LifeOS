package com.project.lifeos.ui.screen

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import co.touchlab.kermit.Logger
import com.project.lifeos.di.AppModule
import com.project.lifeos.viewmodel.HomeScreenViewModel

@Composable
expect fun HomeScreenContent(viewModel: HomeScreenViewModel, navigator: Navigator? = null)


/**
not serializable objects should be outside of class
otherwise there would be an exception after onPause()
 */
private val logger = Logger.withTag("HomeScreen")

class HomeScreen(private val appModule: AppModule) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val homeViewModel = rememberScreenModel { appModule.provideHomeScreenViewModel() }
        logger.i("Home Screen active")
        HomeScreenContent(viewModel = homeViewModel, navigator = navigator)
    }
}