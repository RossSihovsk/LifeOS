package com.project.lifeos.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import co.touchlab.kermit.Logger
import com.project.lifeos.MainActivity
import com.project.lifeos.R
import com.project.lifeos.utils.formatNotificationText


class NotificationSenderReceiver : BroadcastReceiver() {
    private val logger = Logger.withTag("NotificationSenderReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        logger.d("onReceive ${intent.action}")

        val notificationManager =
            ContextCompat.getSystemService(context, NotificationManager::class.java) as NotificationManager

        if (!notificationManager.areNotificationsEnabled()) {
            logger.e("Notifications are not enabled")
            return
        }

        val userName = intent.getStringExtra(USERNAME_EXTRA)
        val title = intent.getStringExtra(TITLE_EXTRA)
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = CHANNEL_DESCRIPTION
        }
        notificationManager.createNotificationChannel(channel)

        val mainActivityIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, mainActivityIntent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentText(formatNotificationText(userName, title!!))
            .setSmallIcon(R.drawable.ic_done)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setContentIntent(pendingIntent)
            .setPublicVersion(
                NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentText("Hey ${userName?:""}, check your list")
                    .setSmallIcon(R.drawable.ic_done)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setTicker(TICKER)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .build()
            )
            .setOngoing(false)
            .setChannelId(CHANNEL_ID)
        notificationManager.notify(1, builder.build())
    }

    companion object {
        const val USERNAME_EXTRA = "user_name"
        const val TITLE_EXTRA = "title"

        private const val CHANNEL_ID = "Important_mail_channe"
        private const val CHANNEL_DESCRIPTION = "No description"
        private const val CHANNEL_NAME = "LifeOs"

        private const val TICKER = "Notification"
    }
}