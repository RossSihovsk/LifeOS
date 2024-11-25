package com.project.lifeos.utils

import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import co.touchlab.kermit.Logger

private val logger = Logger.withTag("Navigator")

fun Navigator.safePush(screen: Screen) {
    logger.d("safePush: ${screen.key}")
    if (items.isNotEmpty()) logger.d("last: ${items.last().key}")
    if (items.isNotEmpty() && items.last().key  == screen.key) {
        logger.d("return")
//    if (items.isNotEmpty() && items.firstOrNull { it.key == screen.key } != null) {
       return
    }
    logger.d("push")
    push(screen)
}