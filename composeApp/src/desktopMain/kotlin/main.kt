import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.project.lifeos.App
import com.project.lifeos.di.DesktopAppModule

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "LifeOS") {
        val appModule = DesktopAppModule()
        App(appModule)
    }
}

