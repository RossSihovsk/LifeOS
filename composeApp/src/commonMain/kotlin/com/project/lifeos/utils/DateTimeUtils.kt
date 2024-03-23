package com.project.lifeos.utils

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Date
import java.util.Locale

fun formatTime(timeInMillis: Long): String {
    val formatter = SimpleDateFormat("h:mm a", Locale.getDefault()) // Customize format if needed
    return formatter.format(Date(timeInMillis))
}

fun convertLocalDateTimeToLong(date: LocalDateTime): Long {

    val zdt = ZonedDateTime.of(date, ZoneId.systemDefault())

    return zdt.toInstant().toEpochMilli()
}