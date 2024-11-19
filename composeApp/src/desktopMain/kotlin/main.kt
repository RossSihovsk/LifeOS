import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.project.lifeos.App
import com.project.lifeos.di.DesktopAppModule

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "LifeOS") {
        App()

//      Desktop example of How to get the auth token
//        runBlocking {
//            val desktop = Desktop.getDesktop()
//            val client = GoogleAuthClient(GOOGLE_CLIENT_ID, GOOGLE_CLIENT_SECRET)
//            println(client.authorizationUrl)
//            desktop.browse(URI(client.authorizationUrl))
//            val res = client.waitForOAuthIdToken()
//            println(res)
//        }
    }
}

