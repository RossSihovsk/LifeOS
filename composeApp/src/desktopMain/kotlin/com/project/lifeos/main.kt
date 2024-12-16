package com.project.lifeos

import androidx.compose.ui.window.Window
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.ui.window.WindowPosition


fun main() = application {
    val state = rememberWindowState(position = WindowPosition(Alignment.Center), size = DpSize(1120.dp, 900.dp))
    Window(onCloseRequest = ::exitApplication, title = "Should", state = state)
    {
        App()
    }
}

