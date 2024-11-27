package com.project.lifeos.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.project.lifeos.data.DateStatus
import com.project.lifeos.data.Priority
import com.project.lifeos.data.Reminder
import com.project.lifeos.data.Task
import com.project.lifeos.notification.NotificationScheduler
import com.project.lifeos.repository.TaskRepository
import com.project.lifeos.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddTaskViewModel(
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository,
    private val notificationScheduler: NotificationScheduler
) : ScreenModel {

    // Flow of tasks for a specific date
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())

    // Local mutable list of tasks
    val task = _tasks.asStateFlow()

    fun saveTask(
        title: String,
        description: String? = null,
        time: Long? = null,
        dates: List<String>,
        checkItems: List<String> = emptyList(),
        reminder: Reminder = Reminder.NONE,
        priority: Priority = Priority.NO_PRIORITY,
        onTaskSaved: (task: Task) -> Unit = {}
    ) {
        val currentTasks = _tasks.value.toMutableList()
        screenModelScope.launch(Dispatchers.Default) {
            val currentUser = userRepository.getLastUser()
            val task = Task(
                title = title,
                description = description,
                time = time,
                dateStatuses = dates.map { date -> DateStatus(date, false) },
                checkItems = checkItems,
                priority = priority,
                reminder = reminder,
                userEmail = currentUser?.mail
            )
            currentTasks.add(task)
            taskRepository.addTask(task)
            notificationScheduler.scheduleNotificationIfNeeded(task, currentUser)
            onTaskSaved(task)
        }
    }
}