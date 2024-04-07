package com.project.lifeos.di

import com.lifeos.LifeOsDatabase
import com.project.lifeos.repository.TaskRepository
import com.project.lifeos.repository.local.LocalDataSource
import com.project.lifeos.ui.view.calendar.CalendarDataSource
import com.project.lifeos.viewmodel.CreateTaskScreenViewModel
import com.project.lifeos.viewmodel.HomeScreenViewModel


// Shared
interface AppModule {
    fun provideLocalDataSource(): LocalDataSource
    fun provideCalendarDataSource(): CalendarDataSource = CalendarDataSource()
    fun provideTaskRepository(): TaskRepository = TaskRepository(
        localDataSource = provideLocalDataSource()
    )

    fun provideHomeScreenViewModel(): HomeScreenViewModel = HomeScreenViewModel(
        calendarDataSource = provideCalendarDataSource(),
        repository = provideTaskRepository()
    )

    fun provideAddTaskViewModel(): CreateTaskScreenViewModel = CreateTaskScreenViewModel(
        repository = provideTaskRepository()
    )
}