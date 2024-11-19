package com.project.lifeos.di

expect object AppModuleProvider {
    fun getAppModule(): AppModule
}