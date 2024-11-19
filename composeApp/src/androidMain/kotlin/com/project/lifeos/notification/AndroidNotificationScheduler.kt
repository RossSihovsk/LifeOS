package com.project.lifeos.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import co.touchlab.kermit.Logger
import com.project.lifeos.data.Task
import com.project.lifeos.data.User
import com.project.lifeos.utils.convertLongToStringDate

private val logger = Logger.withTag("AndroidNotificationScheduler")

class AndroidNotificationScheduler(private val context: Context) : NotificationScheduler() {
    override fun schedulePlatformNotification(task: Task, user: User?, validatedTimeForNotification: List<Long>) {
        logger.d("schedulePlatformNotification")

        if (validatedTimeForNotification.isEmpty()) return

        val alarmManager = ContextCompat.getSystemService(context, AlarmManager::class.java) as AlarmManager

        val intent = Intent(context, NotificationSenderReceiver::class.java).apply {
            action = INTENT_ACTION
            putExtra(NotificationSenderReceiver.TITLE_EXTRA, task.title)
            user?.name?.let { name -> putExtra(NotificationSenderReceiver.USERNAME_EXTRA, name) }
        }

        validatedTimeForNotification.forEach { dateTime ->
            logger.d("Try to schedule for ${convertLongToStringDate(dateTime)}")
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                dateTime.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            try {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    dateTime,
                    pendingIntent
                )
            } catch (e: SecurityException) {
                logger.d("Failed to set alarm: ${e.message}")
            }
        }
    }

    companion object {
        private const val INTENT_ACTION = "com.project.lifeos.notification.REMINDER_INTENT"
    }
}