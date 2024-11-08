package com.project.lifeos.di

import com.project.lifeos.repository.TaskRepository
import com.project.lifeos.repository.local.LocalDataSource
import com.project.lifeos.ui.view.calendar.CalendarDataSource
import com.project.lifeos.viewmodel.AddTaskViewModel
import com.project.lifeos.viewmodel.HomeScreenViewModel
import com.project.lifeos.viewmodel.UserViewModel


/* This Module is shared across both apps
You just have to provide LocalDataSource object that requires
 a Database object that specific for each platform
**/
abstract class AppModule {
    abstract val localDataSource: LocalDataSource

    private val calendarDataSource: CalendarDataSource
        get() = CalendarDataSource()

    private val taskRepository: TaskRepository
        get() = TaskRepository(localDataSource = localDataSource)

    val homeScreenViewModel: HomeScreenViewModel by lazy {
        HomeScreenViewModel(
            calendarDataSource = calendarDataSource,
            repository = taskRepository
        )
    }

    val addTaskViewModel: AddTaskViewModel by lazy {
        AddTaskViewModel(repository = taskRepository)
    }

    val userViewModel: UserViewModel by lazy {
        UserViewModel()
    }
}