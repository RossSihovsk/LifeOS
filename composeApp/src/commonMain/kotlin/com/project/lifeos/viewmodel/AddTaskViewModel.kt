package com.project.lifeos.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.project.lifeos.data.Priority
import com.project.lifeos.data.Reminder
import com.project.lifeos.data.Task
import com.project.lifeos.data.TaskStatus
import com.project.lifeos.repository.TaskRepository
import com.project.lifeos.repository.UserRepository
import com.project.lifeos.utils.convertLongToStringDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class AddTaskViewModel(
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository
) : ScreenModel {

    // Flow of tasks for a specific date
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())

    // Local mutable list of tasks
    val task = _tasks.asStateFlow()

    fun saveTask(
        title: String,
        description: String? = null,
        time: Long? = null,
        dates: String,
        checkItems: List<String> = emptyList(),
        status: TaskStatus = TaskStatus.PENDING,
        reminder: Reminder = Reminder.NONE,
        priority: Priority = Priority.NO_PRIORITY
    ) {
        val currentTasks = _tasks.value.toMutableList()

        screenModelScope.launch(Dispatchers.Default) {
            val task = Task(
                title = title,
                description = description,
                time = time,
                date = dates,
                checkItems = checkItems,
                status = status,
                priority = priority,
                reminder = reminder,
                userEmail = userRepository.getLastUser()?.mail
            )
            currentTasks.add(task)
            taskRepository.addTask(task)
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