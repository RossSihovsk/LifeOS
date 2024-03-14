package com.project.lifeos.repository

import co.touchlab.kermit.Logger
import com.project.lifeos.data.Task
import com.project.lifeos.data.TaskStatus
import com.project.lifeos.utils.generateTasks

class TaskRepository {
    private val taskList = generateTasks(8).toMutableList()
    private val logger = Logger.withTag("TaskRepository")

    suspend fun getTasksForDay(day: Long): List<Task> {
        return taskList
    }

    suspend fun addTask(task: Task) {
        taskList.add(task)
    }

    suspend fun onTaskStatusChanged(status: Boolean, task: Task) {
        val find = taskList.find { localTask -> localTask == task }
        find?.status = if (status) TaskStatus.DONE else TaskStatus.PENDING
    }
}