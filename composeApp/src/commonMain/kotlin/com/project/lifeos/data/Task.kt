package com.project.lifeos.data

import java.time.LocalDate

data class Task(
    val title: String,
    val description: String? = null,
    val time: Long,
    val date: Long,
    var status: TaskStatus = TaskStatus.PENDING
)

enum class TaskStatus {
    PENDING,
    DONE,
    SKIPPED
}