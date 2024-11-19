package com.project.lifeos.notification

import com.project.lifeos.data.Task
import com.project.lifeos.data.User

class DesktopNotificationScheduler : NotificationScheduler() {
    override fun schedulePlatformNotification(task: Task, user: User?, validatedTimeForNotification: List<Long>) {

    }
}