package com.project.lifeos.repository

import co.touchlab.kermit.Logger
import com.project.lifeos.data.User
import com.project.lifeos.repository.local.LocalUserDataSource

class UserRepository(private val localUserDataSource: LocalUserDataSource) {

    private val logger = Logger.withTag("TaskRepository")

    fun saveNewUser(user: User) {
        logger.d("saveNewUser: $user")
        localUserDataSource.saveNewUser(user)
    }

    fun signOut() {
        logger.d("signOut")
        localUserDataSource.signOut()
    }

    fun getLastUser(): User? {
        logger.d("getLastUser")
        return localUserDataSource.getLastUser().also {
            logger.d("result: it")
        }
    }
}