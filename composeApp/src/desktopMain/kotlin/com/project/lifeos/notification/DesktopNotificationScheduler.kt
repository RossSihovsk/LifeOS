package com.project.lifeos.notification

import com.project.lifeos.data.Task
import com.project.lifeos.data.User
import java.awt.*

class DesktopNotificationScheduler : NotificationScheduler() {
    override fun schedulePlatformNotification(task: Task, user: User?, validatedTimeForNotification: List<Long>) {
        showDesktopNotification("zxc","xczzx")
    }

    private fun showDesktopNotification(title: String, message: String) {
        if (!SystemTray.isSupported()) {
            println("SystemTray is not supported")
            return
        }

        val tray = SystemTray.getSystemTray()
        val image = Toolkit.getDefaultToolkit().createImage("no_data.png") // Provide a small icon here

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
}