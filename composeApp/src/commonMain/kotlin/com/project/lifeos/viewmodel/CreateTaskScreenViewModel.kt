package com.project.lifeos.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.project.lifeos.data.Task
import com.project.lifeos.data.TaskStatus
import com.project.lifeos.repository.TaskRepository
import com.project.lifeos.utils.convertLongToStringDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Date

class CreateTaskScreenViewModel(private val repository: TaskRepository) : ScreenModel {

    // Flow of tasks for a specific date
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())

    // Local mutable list of tasks
    val task = _tasks.asStateFlow()

    fun saveTask(
        title: String,
        description: String? = null,
        time: Long? = null,
        date: Long? = null,
        status: TaskStatus = TaskStatus.PENDING
    ) {
        val currentTasks = _tasks.value.toMutableList()

        screenModelScope.launch(Dispatchers.Default) {
            val task = validateAndCreateTask(title,description,time,date,status)
            currentTasks.add(task)
            repository.addTask(task)
        }
    }

    private fun validateAndCreateTask(
        title: String,
        description: String?,
        time: Long?,
        date: Long?,
        status: TaskStatus
    ): Task {

        val stringDate = if (date == null) convertLongToStringDate(Date().time) else convertLongToStringDate(date)

        return Task(
            title = title,
            description = description,
            time = time,
            date = stringDate,
            status = status
        )
    }
}