package com.project.lifeos.repository

import co.touchlab.kermit.Logger
import com.project.lifeos.data.Task
import com.project.lifeos.data.TaskStatus
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

    fun onTaskStatusChanged(status: Boolean, task: Task) {
        task.id?.let {
            val find = localList.find { localTask -> localTask == task }
            find?.status = if (status) TaskStatus.DONE else TaskStatus.PENDING



            localTaskDataSource.updateStatus(task.id, if (status) TaskStatus.DONE else TaskStatus.PENDING)
            _tasksFlow.tryEmit(localList)
            return
        }

        logger.e("Task ID is null. Cannot change status")
    }

    fun updateList(newItems: List<Task>) {
        _tasksFlow.tryEmit(newItems)
    }
}