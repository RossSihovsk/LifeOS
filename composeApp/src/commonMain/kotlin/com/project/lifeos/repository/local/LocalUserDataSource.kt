package com.project.lifeos.repository.local

import co.touchlab.kermit.Logger
import com.lifeos.LifeOsDatabase
import com.lifeos.UserEntity
import com.project.lifeos.data.User

class LocalUserDataSource(db: LifeOsDatabase) {

    private val logger = Logger.withTag("LocalUserDataSource")
    private val queries = db.userEntityQueries

    fun saveNewUser(user: User) {
        logger.d("saveNewUser: $user")
        queries.saveNewUser(
            name = user.name,
            mail = user.mail,
            photo = user.profilePicture,
            tokenId = user.tokenId
        )
    }

    fun getLastUser(): User? {
        logger.d("getLastUser")
        return queries.getLastUserOrNull().executeAsOneOrNull()?.mapToUser()
    }

    fun signOut() {
        queries.deleteLastUser()
    }

    private fun UserEntity.mapToUser(): User {
        return User(id = id, name = name, mail = mail, profilePicture = photo, tokenId = tokenId)
    }
}