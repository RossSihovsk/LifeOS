import androidx.compose.ui.window.Window
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.project.lifeos.App
import com.project.lifeos.di.DesktopAppModule
import androidx.compose.ui.window.WindowPosition
import api.GoogleAuthClient
import kotlinx.coroutines.runBlocking
import java.awt.Desktop
import java.net.URI

fun main() = application {
    val state = rememberWindowState(position = WindowPosition(Alignment.Center), size = DpSize(1120.dp, 900.dp))
    Window(onCloseRequest = ::exitApplication, title = "LifeOS", state = state)
    {
        val appModule = DesktopAppModule()
        App(appModule)
    }
}

