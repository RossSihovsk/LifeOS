package com.project.lifeos.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import com.project.lifeos.viewmodel.TaskViewModel

@Composable
actual fun HomeScreenContent(
    viewModel: TaskViewModel,
    navigator: Navigator?
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Button(onClick = {navigator?.push(AddTaskScreen())}) {
            Text("New screen")
        }
    }
}