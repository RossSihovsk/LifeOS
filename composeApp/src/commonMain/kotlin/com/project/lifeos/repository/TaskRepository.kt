package com.project.lifeos.repository

import co.touchlab.kermit.Logger
import com.project.lifeos.data.Task
import com.project.lifeos.data.TaskStatus
import com.project.lifeos.repository.local.LocalDataSource
import com.project.lifeos.utils.generateTasks

class TaskRepository(private val localDataSource: LocalDataSource) {
    private val taskList = generateTasks(8).toMutableList()
    private val logger = Logger.withTag("TaskRepository")

    fun getTasksForDay(day: String): List<Task> {
        return taskList
    }

    fun addTask(task: Task) {
        localDataSource.insert(task)
//        taskList.add(task)
    }

    fun onTaskStatusChanged(status: Boolean, task: Task) {
        task.id?.let {
            val find = taskList.find { localTask -> localTask == task }
            find?.status = if (status) TaskStatus.DONE else TaskStatus.PENDING



            localDataSource.updateStatus(task.id,if (status) TaskStatus.DONE else TaskStatus.PENDING )
            return
        }

        logger.e("Task ID is null. Cannot change status")
    }
}