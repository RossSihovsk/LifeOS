package com.project.lifeos.di

import android.content.Context
import com.lifeos.LifeOsDatabase
import com.project.lifeos.repository.local.DatabaseDriverFactory
import com.project.lifeos.repository.local.LocalDataSource

class AndroidAppModule(
    private val context: Context,
) : AppModule {

    private val db by lazy {
        LifeOsDatabase(
            driver = DatabaseDriverFactory(context).create()
        )
    }

    override fun provideLocalDataSource(): LocalDataSource {
        return LocalDataSource(db)
    }
}