package com.project.lifeos.data

data class User(
    val id: Long? = null,
    val name: String,
    val mail: String,
    val profilePicture: String?,
    val tokenId: String
)
