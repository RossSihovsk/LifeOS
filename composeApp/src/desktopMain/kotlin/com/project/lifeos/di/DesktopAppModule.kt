package com.project.lifeos.di

import com.lifeos.LifeOsDatabase
import com.project.lifeos.notification.DesktopNotificationScheduler
import com.project.lifeos.notification.NotificationScheduler
import com.project.lifeos.repository.local.DatabaseDriverFactory
import com.project.lifeos.repository.local.LocalGoalDataSource
import com.project.lifeos.repository.local.LocalTaskDataSource
import com.project.lifeos.repository.local.LocalUserDataSource

class DesktopAppModule : AppModule() {

    private val db by lazy {
        LifeOsDatabase(
            driver = DatabaseDriverFactory().create()
        )
    }
    override val localTaskDataSource: LocalTaskDataSource
        get() = LocalTaskDataSource(db)

    override val localUserDataSource: LocalUserDataSource
        get() = LocalUserDataSource(db)

    override val localGoalDataSource: LocalGoalDataSource
        get() = LocalGoalDataSource(db)

    override val notificationScheduler: NotificationScheduler
        get() = DesktopNotificationScheduler()
}