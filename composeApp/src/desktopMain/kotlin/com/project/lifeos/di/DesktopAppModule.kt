package com.project.lifeos.di

import com.lifeos.LifeOsDatabase
import com.project.lifeos.repository.local.DatabaseDriverFactory
import com.project.lifeos.repository.local.LocalDataSource

class DesktopAppModule : AppModule {

    private val db by lazy {
        LifeOsDatabase(
            driver = DatabaseDriverFactory().create()
        )
    }

    override fun provideLocalDataSource(): LocalDataSource {
        return LocalDataSource(db)
    }


}