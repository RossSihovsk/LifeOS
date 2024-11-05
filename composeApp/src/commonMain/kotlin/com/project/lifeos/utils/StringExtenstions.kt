package com.project.lifeos.utils

fun List<String>.convertToSingleLine(): String {
    var finalResult = ""
    forEach { value ->
        finalResult += "$value;"
    }
    return finalResult
}
