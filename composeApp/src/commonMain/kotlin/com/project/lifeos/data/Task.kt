package com.project.lifeos.data

import java.time.LocalDate

data class Task(
    val id: String,
    val title: String,
    val description: String? = null,
    val icon: ActivityIcon,
    val goalId: String? = null,
    val date: LocalDate,
    val repeats: Boolean = false, // Flag indicating if the task is recurring
    val endDate: LocalDate? = null, // Optional end date for the task
    val status: TaskStatus = TaskStatus.PENDING
)

enum class TaskStatus {
    PENDING,
    DONE,
    SKIPPED
}