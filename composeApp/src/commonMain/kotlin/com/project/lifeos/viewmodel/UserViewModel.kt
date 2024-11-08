package com.project.lifeos.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import co.touchlab.kermit.Logger
import com.project.lifeos.data.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

val logger = Logger.withTag("UserViewModel")

class UserViewModel : ScreenModel {
    suspend fun signIn(
        signInPerform: suspend () -> User?
    ) = screenModelScope.launch {
        logger.d("Init")
        signInPerform()?.let { user ->
            logger.d("User founded: $user")
            //save user
            _uiState.emit(ProfileUiState.UserFounded(user))
            return@launch
        }
        logger.d("User was not founded")
        _uiState.emit(ProfileUiState.NoUsers)
    }

    fun signOut() = screenModelScope.launch {
        _uiState.emit(ProfileUiState.NoUsers)
    }

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.NoUsers)
    val uiState = _uiState.asStateFlow()


    companion object {
        const val WEB_CLIENT_ID = "643315953352-oddr832p5238bdifqr6fa42iaboug3mr.apps.googleusercontent.com"
        const val DESKTOP_CLIENT_ID = "643315953352-9qgkse0ffju6nh7ohmi02r1nsmd4ontf.apps.googleusercontent.com"
    }
}

sealed class ProfileUiState {
    data class UserFounded(val user: User) : ProfileUiState()
    data object NoUsers : ProfileUiState()
}