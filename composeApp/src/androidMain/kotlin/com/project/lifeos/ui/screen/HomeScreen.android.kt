package com.project.lifeos.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import com.project.lifeos.viewmodel.HomeViewModel

@Composable
actual fun HomeScreenContent(
    viewModel: HomeViewModel,
    navigator: Navigator
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = viewModel.title())
        Text("Screen Home")
        Button(onClick = {
            navigator.push(AddTaskScreen())
        }) {
            Text("Navigate to add Screen")
        }
    }
}