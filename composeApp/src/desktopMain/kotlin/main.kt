import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import com.project.lifeos.App
import com.project.lifeos.di.DesktopAppModule

fun main() = application {
    val state = rememberWindowState(position = WindowPosition(Alignment.Center), size = DpSize(568.dp, 900.dp))
    Window(onCloseRequest = ::exitApplication, title = "LifeOS", state = state) {
        val appModule = DesktopAppModule()
        App(appModule)
    }
}

