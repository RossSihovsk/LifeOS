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

//fun convertStringDateToLong(date: String): Long = dateFormatter.parse(date).toInstant().toEpochMilli()
//
//fun convertLocalDateTimeToLong(date: LocalDateTime): Long {
//
//    val zdt = ZonedDateTime.of(date, ZoneId.systemDefault())
//
//    return zdt.toInstant().toEpochMilli()
//}