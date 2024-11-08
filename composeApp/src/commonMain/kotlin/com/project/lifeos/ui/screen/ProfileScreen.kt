package com.project.lifeos.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import co.touchlab.kermit.Logger
import com.project.lifeos.di.AppModule
import com.project.lifeos.viewmodel.HomeScreenViewModel
import com.project.lifeos.viewmodel.UserViewModel
import lifeos.composeapp.generated.resources.Res

@Composable
expect fun ProfileScreenContent(viewModel: UserViewModel, navigator: Navigator? = null)


/**
not serializable objects should be outside of class
otherwise there would be an exception after onPause()
 */
private val logger = Logger.withTag("ProfileScreen")

class ProfileScreen(private val appModule: AppModule) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = rememberScreenModel { appModule.userViewModel }
        ProfileScreenContent(viewModel = viewModel,)
    }
}