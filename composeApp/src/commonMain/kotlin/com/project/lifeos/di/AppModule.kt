package com.project.lifeos.di

import com.project.lifeos.repository.TaskRepository
import com.project.lifeos.ui.view.calendar.CalendarDataSource
import com.project.lifeos.viewmodel.CreateTaskScreenViewModel
import com.project.lifeos.viewmodel.HomeScreenViewModel

object AppModule {
    val createTaskScreenViewModel = CreateTaskScreenViewModel()
    val taskRepository = TaskRepository()
    val calendarDataSource = CalendarDataSource()
    val homeViewModel = HomeScreenViewModel()
}