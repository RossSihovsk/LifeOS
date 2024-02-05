package com.project.lifeos.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.project.lifeos.viewmodel.HomeViewModel

@Composable
actual fun HomeScreen(viewModel: HomeViewModel) {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(text = viewModel.title())
    }
}

@Composable
@Preview
fun HomeAndroidPreview () {
//    HomeScreen()
}