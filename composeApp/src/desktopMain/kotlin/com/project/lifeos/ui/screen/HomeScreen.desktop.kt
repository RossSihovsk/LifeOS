package com.project.lifeos.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
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
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = viewModel.title())
        Button(onClick = {navigator.push(AddTaskScreen())}) {
            Text("New screen")
        }
    }
}