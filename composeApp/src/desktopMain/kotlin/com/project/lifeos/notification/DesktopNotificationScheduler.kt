package com.project.lifeos.notification
import com.project.lifeos.data.Task
import com.project.lifeos.data.User
import java.awt.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class DesktopNotificationScheduler : NotificationScheduler() {
    override fun schedulePlatformNotification(task: Task, user: User?, validatedTimeForNotification: List<Long>) {
        task.description?.let { scheduleNotificationWithExecutor(task.title, it, validatedTimeForNotification) }
    }

    private fun showDesktopNotification(title: String, message: String) {
        if (!SystemTray.isSupported()) {
            println("SystemTray is not supported")
            return
        }

        val tray = SystemTray.getSystemTray()
        val image = Toolkit.getDefaultToolkit().createImage("on_time.png") // Provide a small icon here

        val trayIcon = TrayIcon(image, "Notification Example")
        trayIcon.isImageAutoSize = true
        trayIcon.toolTip = "Notification Example"

        try {
            tray.add(trayIcon)
            trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun scheduleNotificationWithExecutor(
        title: String,
        message: String,
        targetDateTime: List<Long>
    ) {
        val scheduler = Executors.newSingleThreadScheduledExecutor()
        targetDateTime.forEach {
            val delay = it - System.currentTimeMillis()
            if (delay <= 0) {
                return
            }
            scheduler.schedule({
                showDesktopNotification(title, message)
                scheduler.shutdown()
            }, delay, TimeUnit.MILLISECONDS)
        }

    }






}