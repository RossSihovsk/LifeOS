package com.project.lifeos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.project.lifeos.di.AndroidAppModule

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContent {
            val androidAppModule = AndroidAppModule(applicationContext)
            App(androidAppModule)
        }
    }
}
