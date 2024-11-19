package com.project.lifeos.di

import android.content.Context

actual object AppModuleProvider {
    private lateinit var appModule: AppModule

    fun init(context: Context) {
        appModule = AndroidAppModule(context)
    }

    actual fun getAppModule(): AppModule {
        return appModule
    }
}