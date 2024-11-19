package com.project.lifeos.utils

fun List<String>.convertToSingleLine(): String {
    var finalResult = ""
    forEach { value ->
        finalResult += "$value;"
    }
    return finalResult
}

fun formatNotificationText(userName: String?, title: String): String {
    return if (userName.isNullOrEmpty()) {
        "Hey, it's time to ${title}\nCheck your all To-Dos for today"
    } else "Hey $userName, it's time to $title\nCheck your all To-Dos for today"
}
