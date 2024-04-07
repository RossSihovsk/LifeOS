package com.project.lifeos.data

import java.time.LocalDate

data class Task(
    val id: Long? = null,
    val title: String,
    val description: String? = null,
    val time: Long?,
    val date: String?,
    var status: TaskStatus = TaskStatus.PENDING
)

enum class TaskStatus {
    PENDING,
    DONE,
    SKIPPED
}