package com.project.lifeos.di

import android.content.Context
import com.lifeos.LifeOsDatabase
import com.project.lifeos.repository.TaskRepository
import com.project.lifeos.repository.local.DatabaseDriverFactory
import com.project.lifeos.repository.local.LocalDataSource
import com.project.lifeos.viewmodel.AddTaskViewModel
import com.project.lifeos.viewmodel.HomeScreenViewModel

class AndroidAppModule(
    private val context: Context,
) : AppModule() {

    private val db by lazy {
        LifeOsDatabase(
            driver = DatabaseDriverFactory(context).create()
        )
    }
    override val localDataSource: LocalDataSource
        get() = LocalDataSource(db)
}