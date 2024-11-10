package com.project.lifeos.di

import com.project.lifeos.repository.TaskRepository
import com.project.lifeos.repository.UserRepository
import com.project.lifeos.repository.local.LocalTaskDataSource
import com.project.lifeos.repository.local.LocalUserDataSource
import com.project.lifeos.ui.view.calendar.CalendarDataSource
import com.project.lifeos.viewmodel.AddTaskViewModel
import com.project.lifeos.viewmodel.HomeScreenViewModel
import com.project.lifeos.viewmodel.UserViewModel


/* This Module is shared across both apps
You just have to provide LocalDataSource object that requires
 a Database object that specific for each platform
**/
abstract class AppModule {
    abstract val localTaskDataSource: LocalTaskDataSource
    abstract val localUserDataSource: LocalUserDataSource

    private val calendarDataSource: CalendarDataSource
        get() = CalendarDataSource()

    private val taskRepository: TaskRepository
        get() = TaskRepository(localTaskDataSource = localTaskDataSource)

    private val userRepository: UserRepository
        get() = UserRepository(localUserDataSource)

    val homeScreenViewModel: HomeScreenViewModel by lazy {
        HomeScreenViewModel(
            calendarDataSource = calendarDataSource,
            taskRepository = taskRepository,
            userRepository = userRepository
        )
    }

    val addTaskViewModel: AddTaskViewModel by lazy {
        AddTaskViewModel(
            taskRepository = taskRepository,
            userRepository = userRepository
        )
    }

    val userViewModel: UserViewModel by lazy {
        UserViewModel(
            userRepository = userRepository,
            taskRepository = taskRepository
        )
    }
}