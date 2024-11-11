package com.project.lifeos.di

import android.content.Context
import com.lifeos.LifeOsDatabase
import com.project.lifeos.repository.local.DatabaseDriverFactory
import com.project.lifeos.repository.local.LocalTaskDataSource
import com.project.lifeos.repository.local.LocalUserDataSource

class AndroidAppModule(
    private val context: Context,
) : AppModule() {

    private val db by lazy {
        LifeOsDatabase(
            driver = DatabaseDriverFactory(context).create()
        )
    }
    override val localTaskDataSource: LocalTaskDataSource
        get() = LocalTaskDataSource(db)

    override val localUserDataSource: LocalUserDataSource
        get() = LocalUserDataSource(db)
}