package com.project.lifeos.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import cafe.adriel.voyager.navigator.Navigator
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.project.lifeos.R
import com.project.lifeos.data.User
import com.project.lifeos.ui.view.makeToastMessage
import com.project.lifeos.viewmodel.ProfileUiState
import com.project.lifeos.viewmodel.UserViewModel
import com.project.lifeos.viewmodel.UserViewModel.Companion.WEB_CLIENT_ID
import kotlinx.coroutines.launch
import java.lang.Exception
import java.security.MessageDigest
import java.util.UUID

@Composable
actual fun ProfileScreenContent(
    viewModel: UserViewModel,
    navigator: Navigator?
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(top = 50.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val uiState by viewModel.uiState.collectAsState()
        val context = LocalContext.current
        val credentialManager = CredentialManager.create(context)
        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(Unit) {
            viewModel.getLastSignedUser()
        }


        when (val state = uiState) {

            is ProfileUiState.NoUsers -> {
                NoUserView {
                    coroutineScope.launch {
                        viewModel.signIn {
                            val rawNonce = UUID.randomUUID().toString()
                            val bytes = rawNonce.toByteArray()
                            val md = MessageDigest.getInstance("SHA-256")
                            val digest = md.digest(bytes)
                            val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }


                            val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                                .setFilterByAuthorizedAccounts(false)
                                .setServerClientId(WEB_CLIENT_ID)
                                .setAutoSelectEnabled(false)
                                .setNonce(hashedNonce)
                                .build()

                            val request = GetCredentialRequest.Builder()
                                .addCredentialOption(googleIdOption)
                                .build()
                            try {
                                val result = credentialManager.getCredential(
                                    request = request,
                                    context = context
                                )
                                val credential = result.credential
                                val userCred = GoogleIdTokenCredential.createFrom(credential.data)
                                println(userCred)
                                User(
                                    name = userCred.displayName ?: "Unknown name",
                                    mail = if (userCred.id.contains("@")) userCred.id else "Unknown email",
                                    profilePicture = userCred.profilePictureUri.toString(),
                                    tokenId = userCred.idToken
                                )
                            } catch (e: Exception) {
                                null
                            }
                        }
                    }
                }
            }

            is ProfileUiState.UserFounded -> {
                UserFoundedView(user = state.user,
                    onClickSignOut = {
                        coroutineScope.launch {
                            credentialManager.clearCredentialState(ClearCredentialStateRequest())
                            viewModel.signOut()
                        }
                    }, onClickDeleteUserData = {
                        viewModel.deleteAllDataForUser(userMail = state.user.mail)
                        makeToastMessage("All Data were deleted!", context)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
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
                painter = painterResource(R.drawable.delete),
                contentDescription = null,
                modifier = Modifier.size(25.dp)
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
                painter = painterResource(R.drawable.google),
                contentDescription = null,
                modifier = Modifier.size(25.dp)
            )

            Spacer(modifier = Modifier.width(5.dp))

            Text(
                text = "Sign Out from google",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ProfileInfo(user: User) {
    Row(horizontalArrangement = Arrangement.spacedBy(20.dp), verticalAlignment = Alignment.CenterVertically) {
        GlideImage(
            model = user.profilePicture, contentDescription = null, contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(75.dp)
                .clip(CircleShape)
                .border(2.dp, Color.Black, CircleShape)
        )
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
        painter = painterResource(R.drawable.no_profile),
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
                painter = painterResource(R.drawable.google),
                contentDescription = null,
                modifier = Modifier.size(25.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = "Sign in with Google",
                fontWeight = FontWeight.Bold
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