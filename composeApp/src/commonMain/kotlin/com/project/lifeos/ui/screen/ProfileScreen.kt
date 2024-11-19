package com.project.lifeos.ui.screen

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import co.touchlab.kermit.Logger
import com.project.lifeos.di.AppModuleProvider
import com.project.lifeos.viewmodel.UserViewModel

@Composable
expect fun ProfileScreenContent(viewModel: UserViewModel, navigator: Navigator? = null)


/**
not serializable objects should be outside of class
otherwise there would be an exception after onPause()
 */
private val logger = Logger.withTag("ProfileScreen")

class ProfileScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = rememberScreenModel { AppModuleProvider.getAppModule().userViewModel }
        ProfileScreenContent(viewModel = viewModel)
    }
}