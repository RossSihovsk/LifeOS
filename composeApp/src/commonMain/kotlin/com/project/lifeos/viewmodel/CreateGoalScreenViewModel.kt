package com.project.lifeos.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import co.touchlab.kermit.Logger
import com.project.lifeos.ai.GeminiApi
import com.project.lifeos.ai.TaskResponse
import com.project.lifeos.data.Category
import com.project.lifeos.data.DateStatus
import com.project.lifeos.data.Duration
import com.project.lifeos.data.Goal
import com.project.lifeos.data.Priority
import com.project.lifeos.data.Reminder
import com.project.lifeos.data.Task
import com.project.lifeos.data.User
import com.project.lifeos.notification.NotificationScheduler
import com.project.lifeos.repository.GoalRepository
import com.project.lifeos.repository.TaskRepository
import com.project.lifeos.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val logger = Logger.withTag("CreateGoalScreenViewModel")

class CreateGoalScreenViewModel(
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository,
    private val goalRepository: GoalRepository,
    private val notificationScheduler: NotificationScheduler,
    private val geminiApi: GeminiApi
) : ScreenModel {

    fun validateDataAndGetTask(
        title: String,
        description: String?,
        time: Long?,
        dates: List<DateStatus>,
        checkItems: List<String>,
        reminder: Reminder,
        priority: Priority,
    ): Task {
        val currentUser = userRepository.getLastUser()
        val task = Task(
            title = title,
            description = description,
            time = time,
            dateStatuses = dates,
            checkItems = checkItems,
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

    fun generateTasksWithAI(
        goalTitle: String,
        goalDescription: String,
        duration: Duration,
        onDone: (tasks: List<TaskResponse>) -> Unit
    ) {
        screenModelScope.launch(Dispatchers.IO) {
            logger.d("generateTasksWithAI")
            val result = geminiApi.generateTasksForGoal(
                title = goalTitle,
                description = goalDescription,
                duration = duration
            )
            logger.d("generateTasksWithAI done $result")
            onDone(
                result
            )
        }
    }

    suspend fun findTasksForGoal(id: String?): List<Task> = screenModelScope.async(Dispatchers.IO) {
        if (id == null) emptyList()
        else taskRepository.getTaskForGoal(id)
    }.await()

    suspend fun convertGeneratedTasks(list: List<TaskResponse>): List<Task> = screenModelScope.async(Dispatchers.IO) {
        val tasks = mutableListOf<Task>()
        val currentUser = userRepository.getLastUser()
        list.forEach { response ->
            tasks.add(response.toTask(user = currentUser))
        }

        tasks
    }.await()

    fun TaskResponse.toTask(user: User?): Task {
        // Convert time (String) to Long (milliseconds)
        val parsedTime = parseTimeToMillis(this.time)

        // Convert dates (List<String>) to List<DateStatus>
        val dateStatuses = this.dates.map { dateStr ->
            DateStatus(date = dateStr, status = false) // You can set the default status to false
        }

        // Convert priority (String) to Priority enum
        val priorityEnum = Priority.getFromTitle(this.priority)

        return Task(
            title = this.title,
            description = this.description,
            time = parsedTime,
            dateStatuses = dateStatuses,
            priority = priorityEnum,
            reminder = Reminder.HOUR_BEFORE,
            userEmail = user?.mail
        )
    }

    // Helper function to convert a time String (e.g., "19:00") to milliseconds (Long)
    fun parseTimeToMillis(time: String): Long? {
        return try {
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
            val localTime = LocalTime.parse(time, timeFormatter)
            localTime.toSecondOfDay() * 1000L // Convert time to milliseconds
        } catch (e: Exception) {
            null // Return null if parsing fails
        }
    }

}