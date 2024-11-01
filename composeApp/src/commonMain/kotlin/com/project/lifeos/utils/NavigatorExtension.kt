package com.project.lifeos.utils

import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator

fun Navigator.safePush(screen: Screen) {
    if (items.isNotEmpty() && items.last().key == screen.key) {
        return
    }
    push(screen)
}