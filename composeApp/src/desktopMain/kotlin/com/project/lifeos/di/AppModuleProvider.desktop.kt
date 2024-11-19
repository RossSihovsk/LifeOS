package com.project.lifeos.di

actual object AppModuleProvider {

    private val appModule = DesktopAppModule()
    actual fun getAppModule(): AppModule {
        return appModule
    }
}