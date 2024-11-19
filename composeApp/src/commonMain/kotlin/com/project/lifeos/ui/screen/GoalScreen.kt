package com.project.lifeos.ui.screen

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import co.touchlab.kermit.Logger
import com.project.lifeos.di.AppModule


@Composable
expect fun GoalScreenContent()

private const val TAG = "GoalScreen"
private val logger = Logger.withTag(TAG)

class GoalScreen : Screen {
    @Composable
    override fun Content() {
//        val viewModel = rememberScreenModel { appModule.addTaskViewModel }
        GoalScreenContent()
    }
}