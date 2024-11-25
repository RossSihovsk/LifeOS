package com.project.lifeos.notification

import co.touchlab.kermit.Logger
import com.project.lifeos.data.Reminder
import com.project.lifeos.data.Task
import com.project.lifeos.data.User
import com.project.lifeos.utils.convertLongToStringDate
import com.project.lifeos.utils.convertToCombinedDateTime
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

private val logger = Logger.withTag("NotificationScheduler")

abstract class NotificationScheduler {
    fun scheduleNotificationIfNeeded(task: Task, user: User?) {
//        if (task.dates.isNullOrEmpty() || task.time == null) {
//            logger.d("Dates are empty for this task")
//            return
//        }
//
//        if (task.reminder == Reminder.NONE) {
//            logger.d("Reminder is not required")
//            return
//        }

        val validatedTimeForNotification = mutableListOf<Long>()
        convertToCombinedDateTime(task.dates, task.time)?.forEach { dateTime ->
            logger.d("Check ${convertLongToStringDate(dateTime)} for ${task.reminder}")
            val result = isReminderStillValid(dateTime, task.reminder)
            if (result.first) {
                val updatedTime = result.second
                logger.d("Valid  ${convertLongToStringDate(updatedTime)}")
                validatedTimeForNotification.add(updatedTime)
                return@forEach
            }
            logger.e("Not valid  ${convertLongToStringDate(dateTime)} for Reminder: ${task.reminder}")
        }

        schedulePlatformNotification(task, user, validatedTimeForNotification)
    }

    private fun isReminderStillValid(dateTime: Long, reminder: Reminder): Pair<Boolean, Long> {
        // Convert long dateTime to LocalDateTime
        val eventTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(dateTime), ZoneId.systemDefault())
        // Get the current time
        val currentTime = LocalDateTime.now()
        // Subtract the reminder duration from the event time
        val reminderTime = eventTime.minus(reminder.duration)
        // Convert LocalDateTime to ZonedDateTime using the system's default time zone
        val zonedDateTime: ZonedDateTime = reminderTime.atZone(ZoneId.systemDefault())
        // Return true if the current time is before the reminder time, false otherwise
        return Pair(currentTime.isBefore(reminderTime), zonedDateTime.toInstant().toEpochMilli())
    }

    internal abstract fun schedulePlatformNotification(
        task: Task,
        user: User?,
        validatedTimeForNotification: List<Long>
    )
}