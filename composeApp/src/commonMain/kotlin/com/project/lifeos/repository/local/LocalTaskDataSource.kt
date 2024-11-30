package com.project.lifeos.repository.local

import co.touchlab.kermit.Logger
import com.lifeos.LifeOsDatabase
import com.lifeos.TaskEntity
import com.project.lifeos.data.DateStatus
import com.project.lifeos.data.Priority
import com.project.lifeos.data.Reminder
import com.project.lifeos.data.Task
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class LocalTaskDataSource(db: LifeOsDatabase) {

    private val logger = Logger.withTag("LocalDataSource")
    private val queries = db.taskEntityQueries

    fun insert(task: Task) {
        logger.d("Insert $task")
        queries.addNewTask(
            id = null,
            name = task.title,
            description = task.description,
            time = task.time,
            dateStatuses = Json.encodeToString(task.dateStatuses),
            checkItems = Json.encodeToString(task.checkItems),
            reminder = task.reminder.title,
            priority = task.priority.title,
            userMail = task.userEmail,
            goalId = task.goalId
        )
    }

    fun updateStatus(id: Long, dateStatuses: List<DateStatus>) {
        logger.d("Update task id: $id new dateStatus: $dateStatuses")
        queries.updateStatus(Json.encodeToString(dateStatuses), id)
    }

    fun getForSomeDay(date: String, userEmail: String?): List<Task> {
        val result = queries.getTasksForDay(
            date = date, userMail = userEmail
        ).executeAsList().mapToTaskList()
        logger.d("getForDay $date result: $result")
        return result
    }

    fun getTaskForGoal(id: String?): List<Task> {
        return queries.getTasksForGoal(id).executeAsList().mapToTaskList()
    }

    fun delete(id: Long) {
        queries.delete(id = id)
    }

    fun deleteAllForUser(userEmail: String) {
        logger.w("Delete all tasks for user: $userEmail")
        queries.deleteAllForUser(userEmail)
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
                    dateStatuses = Json.decodeFromString(taskEntity.dateStatuses),
                    checkItems = taskEntity.checkItems?.let { Json.decodeFromString(it) },
                    reminder = Reminder.getFromTitle(taskEntity.reminder),
                    priority = Priority.getFromTitle(taskEntity.priority),
                    goalId = taskEntity.goalId
                )
            )
        }
        return taskList
    }

    fun deleteTaskForGoal(id: String) {
        queries.deleteForGoal(id)
    }
}
