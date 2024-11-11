package com.project.lifeos.utils

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


private val timeFormatter = SimpleDateFormat("h:mm a", Locale.getDefault())
private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
fun formatTime(timeInMillis: Long): String = timeFormatter.format(Date(timeInMillis))
fun convertLongToStringDate(date: Long): String = dateFormatter.format(date)
fun convertLocalDateToString(localDate: LocalDate): String = localDate.format(dateTimeFormatter)

//Print either today/tomorrow or oct 30 for example
fun formatDateFromLocalDate(date: LocalDate): String {
    // Convert Long timestamp to LocalDate
    // Get today's and tomorrow's date
    val today = LocalDate.now()
    val tomorrow = today.plusDays(1)

    // Compare and format the date accordingly
    return when (date) {
        today -> "Today"
        tomorrow -> "Tomorrow"
        else -> date.format(DateTimeFormatter.ofPattern("dd MMM")) // Example: "30 Oct"
    }
}