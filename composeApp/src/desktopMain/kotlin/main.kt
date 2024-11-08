
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material3.Icon

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.project.lifeos.App
import com.project.lifeos.di.DesktopAppModule

fun main() = application {
        val state = rememberWindowState(position = WindowPosition(Alignment.Center), size = DpSize(1120.dp, 900.dp))
        Window(onCloseRequest = ::exitApplication, title = "LifeOS", state = state )
         {
        val appModule = DesktopAppModule()
        App(appModule)
    }
}

