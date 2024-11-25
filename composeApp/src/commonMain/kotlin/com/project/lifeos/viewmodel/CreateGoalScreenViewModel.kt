package com.project.lifeos.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.project.lifeos.data.Category
import com.project.lifeos.data.Duration
import com.project.lifeos.data.Goal
import com.project.lifeos.data.Priority
import com.project.lifeos.data.Reminder
import com.project.lifeos.data.Task
import com.project.lifeos.data.TaskStatus
import com.project.lifeos.notification.NotificationScheduler
import com.project.lifeos.repository.GoalRepository
import com.project.lifeos.repository.TaskRepository
import com.project.lifeos.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreateGoalScreenViewModel(
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository,
    private val goalRepository: GoalRepository,
    private val notificationScheduler: NotificationScheduler
) : ScreenModel {

    fun validateDataAndGetTask(
        title: String,
        description: String?,
        time: Long?,
        dates: List<String>,
        checkItems: List<String>,
        status: TaskStatus,
        reminder: Reminder,
        priority: Priority,
    ): Task {
        val currentUser = userRepository.getLastUser()
        val task = Task(
            title = title,
            description = description,
            time = time,
            dates = dates,
            checkItems = checkItems,
            status = status,
            priority = priority,
            reminder = reminder,
            userEmail = currentUser?.mail
        )

        return task
    }

    fun saveGoal(
        title: String,
        description: String,
        duration: Duration,
        category: Category,
        tasks: List<Task>
    ) = screenModelScope.launch(Dispatchers.IO) {
        val user = userRepository.getLastUser()
        val goal = Goal(
            title = title,
            shortPhrase = description,
            category = category,
            duration = duration,
            userMail = user?.mail
        )

        goalRepository.saveGoal(goal)

        tasks.forEach {
            taskRepository.addTask(
                it.withGoalId(goal.id)
            )
            notificationScheduler.scheduleNotificationIfNeeded(it, user)
        }
    }

}