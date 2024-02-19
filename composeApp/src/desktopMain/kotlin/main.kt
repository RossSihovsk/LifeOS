import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.project.lifeos.App


fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "LifeOS") {
        App()
}

@Preview
@Composable
fun AppDesktopPreview() {
    App()
}}

