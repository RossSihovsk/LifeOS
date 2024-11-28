package com.project.lifeos.utils

import com.project.lifeos.data.Task
import java.util.Calendar
import java.util.Date
import kotlin.random.Random

//fun generateTasks(numberOfTasks: Int): List<Task> {
//    val tasks = mutableListOf<Task>()
//    val currentCalendar = Calendar.getInstance()
//    currentCalendar.set(Calendar.HOUR_OF_DAY, 0) // Set time to midnight
//    currentCalendar.set(Calendar.MINUTE, 0)
//    currentCalendar.set(Calendar.SECOND, 0)
//    currentCalendar.set(Calendar.MILLISECOND, 0)
//    val currentDate = currentCalendar.timeInMillis
//
//    val random = Random
//
//    for (i in 0 until numberOfTasks) {
//        val randomTimeOffset = random.nextLong(86400000) // Random offset within a day (24 hours in milliseconds)
//        val taskTime = currentDate + randomTimeOffset
//        tasks.add(
//            Task(
//                title = "Task $i",
//                time = taskTime,
//                dateStatuses = listOf(Date(currentDate).toString()),
//                status = if (random.nextBoolean()) TaskStatus.DONE else TaskStatus.PENDING
//            )
//        )
//    }
//
//    return tasks
//}