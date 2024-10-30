package com.project.lifeos.data

import androidx.compose.ui.graphics.Color

data class Task(
    val id: Long? = null,
    val title: String,
    val description: String? = null,
    val time: Long?,
    val date: String?,
    var status: TaskStatus = TaskStatus.PENDING,
    val reminder: Reminder = Reminder.NONE,
    val priority: Priority = Priority.NO_PRIORITY
)

enum class TaskStatus {
    PENDING,
    DONE,
    SKIPPED
}

enum class Reminder(val title: String) {
    NONE("None"),
    ON_TIME("On time"),
    FIVE_MIN_BEFORE("5 minutes early"),
    THIRTY_MIN_BEFORE("30 minutes early"),
    HOUR_BEFORE("1 hour early"),
    DAY_BEFORE("1 day early")
}

enum class Repeat(val title: String) {
    NONE("None"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly"),
    CUSTOM("Custom")
}

enum class Priority(val title: String, val color: Color) {
    HIGH("High priority", Color.Red),
    MEDIUM("Medium priority", Color.Yellow),
    LOW("Low priority", Color.Blue),
    NO_PRIORITY("No priority", Color.Black),
}

