package com.project.lifeos.repository.local

import co.touchlab.kermit.Logger
import com.lifeos.LifeOsDatabase
import com.lifeos.TaskEntity
import com.project.lifeos.data.Priority
import com.project.lifeos.data.Reminder
import com.project.lifeos.data.Task
import com.project.lifeos.data.TaskStatus
import com.project.lifeos.utils.convertToSingleLine

class LocalDataSource(db: LifeOsDatabase) {

    private val logger = Logger.withTag("LocalDataSource")
    private val queries = db.taskEntityQueries

    fun insert(task: Task) {
        logger.d("Insert $task")
        queries.addNewTask(
            id = null,
            name = task.title,
            description = task.description,
            time = task.time,
            date = task.date,
            checkItems = task.checkItems.convertToSingleLine(),
            status = task.status.name,
            reminder = task.reminder.title,
            priority = task.priority.title
        )
    }

    fun updateStatus(id: Long, status: TaskStatus) {
        logger.d("Update task id: $id new status: $status")
        queries.updateStatus(status.name, id)
    }

    fun getForSomeDay(date: String): List<Task> {
        val result = queries.getTasksForDay(date).executeAsList().mapToTaskList()
        logger.d("getForDay $date result: $result")
        return result
    }

    fun delete(id: Long) {
        queries.delete(id = id)
    }

    private fun List<TaskEntity>.mapToTaskList(): List<Task> {
        val taskList = arrayListOf<Task>().toMutableList()
        this.forEach { taskEntity ->
            taskList.add(
                Task(
                    id = taskEntity.id,
                    title = taskEntity.name,
                    description = taskEntity.description,
                    time = taskEntity.time,
                    date = taskEntity.date,
                    checkItems = validateToList(taskEntity.checkItems),
                    status = TaskStatus.valueOf(taskEntity.status),
                    reminder = Reminder.getFromTitle(taskEntity.reminder),
                    priority = Priority.getFromTitle(taskEntity.priority)
                )
            )
        }
        return taskList
    }

    private fun validateToList(checkItems: String?): List<String> {
        checkItems?.let { checkItemsList ->
            return checkItemsList.split(";").filter { value -> value.isNotBlank() }.map { value ->
                value.trimStart()
                value.trimEnd()
            }.toList()
        }
        return emptyList()
    }
}
