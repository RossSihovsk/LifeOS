package com.project.lifeos.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import co.touchlab.kermit.Logger
import com.project.lifeos.data.User
import com.project.lifeos.repository.GoalRepository
import com.project.lifeos.repository.TaskRepository
import com.project.lifeos.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private val logger = Logger.withTag("UserViewModel")

class UserViewModel(
    private val userRepository: UserRepository,
    private val taskRepository: TaskRepository,
    private val goalRepository: GoalRepository
) :
    ScreenModel {
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.NoUsers)
    val uiState = _uiState.asStateFlow()

    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()
    fun signIn(
        signInPerform: suspend () -> User?
    ) = screenModelScope.launch(Dispatchers.IO) {
        logger.d("Init")
        signInPerform()?.let { user ->
            logger.d("User founded: $user")
            userRepository.saveNewUser(user)
            _user.emit(userRepository.getLastUser())
            _uiState.emit(ProfileUiState.UserFounded(user))
            return@launch
        }
        logger.d("User was not founded")
        _uiState.emit(ProfileUiState.NoUsers)
    }

    fun signOut() = screenModelScope.launch(Dispatchers.IO) {
        _uiState.emit(ProfileUiState.NoUsers)
        userRepository.signOut()
        _user.emit(null)
    }

    fun deleteAllDataForUser(userMail: String) {
        screenModelScope.launch(Dispatchers.IO) {
            logger.d("deleteAllDataForUser: $userMail")
            taskRepository.deleteAllForUser(userMail)
            goalRepository.deleteAllForUser(userMail)
        }
    }

    fun getLastSignedUser() = screenModelScope.launch(Dispatchers.IO) {
        logger.d("getLastSignedUser")
        userRepository.getLastUser()?.let { user ->
            logger.d("User founded: $user")
            _user.emit(user)
            _uiState.emit(ProfileUiState.UserFounded(user))
            return@launch
        }
        logger.d("User was not founded")
        _uiState.emit(ProfileUiState.NoUsers)
    }


    companion object {
        const val WEB_CLIENT_ID = "643315953352-oddr832p5238bdifqr6fa42iaboug3mr.apps.googleusercontent.com"
        const val DESKTOP_CLIENT_ID = "643315953352-9qgkse0ffju6nh7ohmi02r1nsmd4ontf.apps.googleusercontent.com"
    }
}

sealed class ProfileUiState {
    data class UserFounded(val user: User) : ProfileUiState()
    data object NoUsers : ProfileUiState()
}