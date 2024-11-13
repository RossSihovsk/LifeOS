package com.project.lifeos.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import api.GoogleAuthClient
import cafe.adriel.voyager.navigator.Navigator
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import com.project.lifeos.data.User
import com.project.lifeos.viewmodel.ProfileUiState
import com.project.lifeos.viewmodel.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.awt.Desktop
import java.net.URI
import java.net.URL
import javax.imageio.ImageIO

@Composable
actual fun ProfileScreenContent(
    viewModel: UserViewModel, navigator: Navigator?
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(top = 50.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val uiState by viewModel.uiState.collectAsState()

        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(Unit) {
            viewModel.getLastSignedUser()
        }
        when (val state = uiState) {
            is ProfileUiState.NoUsers -> {
                NoUserView {
                    coroutineScope.launch {
                        viewModel.signIn {
                            val desktop = Desktop.getDesktop()
                            val client = GoogleAuthClient(
                                "643315953352-9qgkse0ffju6nh7ohmi02r1nsmd4ontf.apps.googleusercontent.com",
                            )
                            println(client.authorizationUrl)
                            desktop.browse(URI(client.authorizationUrl))
                            val res = client.waitForOAuthIdToken()
                            println(res)

                            val userData = getUserDataFromToken(res)
                            println(userData)
                            User(
                                name = userData["name"].toString(),
                                mail = if (userData["email"].toString().contains("@")) userData["email"].toString() else "Unknown email",
                                profilePicture = "https://lh3.googleusercontent.com/a/ACg8ocI4YNuYaVpfYOCl6QWGb8g412h5LuiqFCnDL_6H2Wo-t9K-XI2m=s288-c-no",
                                tokenId = userData["userID"].toString()
                            )

                        }
                    }
                }
            }

            is ProfileUiState.UserFounded -> {
                UserFoundedView(user = state.user, onClickSignOut = {
                    coroutineScope.launch {
                     //   credentialManager.clearCredentialState(ClearCredentialStateRequest())
                        viewModel.signOut()
                    }
                }, onClickDeleteUserData = {
                    viewModel.deleteAllDataForUser(userMail = state.user.mail)
                   // makeToastMessage("All Data were deleted!", context)
                })
            }
        }
    }
}

fun getUserDataFromToken(idToken: String): Map<String, String?> {
    // Розшифровуємо токен
    val decodedJWT: DecodedJWT = JWT.decode(idToken)
    println("decoded JWT: ${decodedJWT.claims}")
    // Отримуємо інформацію про користувача
    val userData = mapOf(
        "userId" to decodedJWT.getClaim("iat").asString(), // Ідентифікатор користувача
        "email" to decodedJWT.getClaim("email").asString(), // Email користувача
        "name" to decodedJWT.getClaim("name").asString(), // Ім'я користувача
        "picture" to decodedJWT.getClaim("picture").asString() // URL фото профілю
    )
    return userData
}

@Composable
fun UserFoundedView(user: User, onClickSignOut: () -> Unit, onClickDeleteUserData: () -> Unit) {
    ProfileInfo(user)

    Spacer(modifier = Modifier.height(20.dp))

    Text(
        text = "You have successfully logged into the application",
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.labelLarge,
        fontSize = 15.sp,
        maxLines = 1,
        overflow = TextOverflow.Visible,
    )

    Spacer(modifier = Modifier.height(60.dp))

    AccountInteractionBox(onClickSignIn = onClickDeleteUserData) {
        Row(modifier = Modifier.wrapContentSize(), horizontalArrangement = Arrangement.Center) {
            Icon(
                Icons.Rounded.Delete, contentDescription = null, modifier = Modifier.size(25.dp)
            )

            Spacer(modifier = Modifier.width(5.dp))

            Text(
                text = "Delete All data",

                fontWeight = FontWeight.Bold
            )
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    AccountInteractionBox(onClickSignIn = onClickSignOut) {

        Row(modifier = Modifier.wrapContentSize(), horizontalArrangement = Arrangement.Center) {
            Icon(
                painter = painterResource("google.png"), contentDescription = null, modifier = Modifier.size(25.dp)
            )

            Spacer(modifier = Modifier.width(5.dp))

            Text(
                text = "Sign Out from google", fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ProfileInfo(user: User) {
    Row(horizontalArrangement = Arrangement.spacedBy(20.dp), verticalAlignment = Alignment.CenterVertically) {
        val image = loadImage(user.profilePicture.toString())

        image?.let {
            Image(bitmap = it, contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(75.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Black, CircleShape))
        }
        Column {
            Text(
                text = user.name,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Visible
            )
            Row {
                Text(
                    text = "Mail: ",
                    fontSize = 10.sp,
                    maxLines = 1,
                )
                Text(
                    text = user.mail,
                    fontSize = 10.sp,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
fun NoUserView(onClickSignIn: () -> Unit) {
    Image(
        painter = painterResource("no_profile.png"),
        contentDescription = null,
    )

    Spacer(modifier = Modifier.height(20.dp))

    Text(
        text = "Nothing about current user",
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.labelLarge,
        fontSize = 20.sp
    )

    Spacer(modifier = Modifier.height(40.dp))

    AccountInteractionBox(onClickSignIn = onClickSignIn) {
        Row(modifier = Modifier.wrapContentSize(), horizontalArrangement = Arrangement.Center) {
            Icon(
                painter = painterResource("google.png"), contentDescription = null, modifier = Modifier.size(25.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = "Sign in with Google", fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun AccountInteractionBox(onClickSignIn: () -> Unit, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.background(Color.Transparent).wrapContentSize(),
        onClick = onClickSignIn,
        border = BorderStroke(1.dp, Color.LightGray),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = Color.Transparent),
    ) {
        Row(
            modifier = Modifier.wrapContentSize().padding(horizontal = 20.dp, vertical = 15.dp),
            horizontalArrangement = Arrangement.spacedBy(1.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            content()
        }
    }
}

@Composable
fun loadImage(url: String): ImageBitmap? {
    var image by remember { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(url) {
        withContext(Dispatchers.IO) {
            try {
                val stream = URL(url).openStream()
                image = ImageIO.read(stream).toComposeImageBitmap()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    return image
}
