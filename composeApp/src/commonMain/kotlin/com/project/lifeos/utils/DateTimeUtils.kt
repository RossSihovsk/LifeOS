package com.project.lifeos.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatTime(timeInMillis: Long): String {
    val formatter = SimpleDateFormat("h:mm a", Locale.getDefault()) // Customize format if needed
    return formatter.format(Date(timeInMillis))
}