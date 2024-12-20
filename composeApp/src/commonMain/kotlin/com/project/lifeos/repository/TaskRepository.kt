package com.project.lifeos.repository

import co.touchlab.kermit.Logger
import com.project.lifeos.data.DateStatus
import com.project.lifeos.data.Priority
import com.project.lifeos.data.Reminder
import com.project.lifeos.data.Task
import com.project.lifeos.repository.local.LocalTaskDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TaskRepository(private val localTaskDataSource: LocalTaskDataSource) {
    // Initialize a MutableStateFlow to hold a list
    private val localList = emptyList<Task>().toMutableList()
    private val _tasksFlow = MutableStateFlow<List<Task>>(emptyList())
    val tasksFlow: StateFlow<List<Task>> = _tasksFlow

    // Function to update the list

    private val logger = Logger.withTag("TaskRepository")

    init {
        logger.d("Init")
    }

    fun getTasksForDay(day: String, userMail: String?): List<Task> {
        logger.d("getTasksForDay day: $day, user: $userMail")
        return localTaskDataSource.getForSomeDay(day, userMail)
    }

    fun getTaskForGoal(id: String?): List<Task> {
        logger.d("getTaskForGoal: $id")
        return localTaskDataSource.getTaskForGoal(id)
    }

    fun deleteAllForUser(userMail: String) {
        logger.d("deleteAllForUser: $userMail")
        localTaskDataSource.deleteAllForUser(userMail)
    }

    fun addTask(task: Task) {
        logger.d("add Task $task")
        localTaskDataSource.insert(task)
        localList.add(task)
        logger.d("try to emit")
        _tasksFlow.tryEmit(localList)
    }

    fun onTaskStatusChanged(newTaskStatus: DateStatus, task: Task) {
        task.dateStatuses.let { dateStatuses ->
            val list = dateStatuses.toMutableList()
            list.removeIf { it.date == newTaskStatus.date }
            list.add(newTaskStatus)

            localTaskDataSource.updateStatus(task.id!!, list)
        }
    }

    fun updateTaskDates(dates: List<DateStatus>, id: Long) {
        if (dates.isEmpty()) deleteCompletely(id)
        else localTaskDataSource.updateStatus(id, dates)
    }

    fun deleteCompletely(id: Long) {
        localTaskDataSource.delete(id)
    }

    fun deleteTaskForGoal(id: String) {
        localTaskDataSource.deleteTaskForGoal(id)
    }

    fun updateTask(
        id: Long,
        title: String,
        description: String?,
        time: Long?,
        dates: List<DateStatus>,
        checkItems: List<String>,
        reminder: Reminder,
        priority: Priority
    ) {
        localTaskDataSource.updateTask(id, title, description, time, dates, checkItems, reminder, priority)
    }
}