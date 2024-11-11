package com.project.lifeos.data

import androidx.compose.ui.graphics.Color

data class Task(
    val id: Long? = null,
    val title: String,
    val description: String? = null,
    val time: Long?,
    val date: String?,
    val checkItems: List<String> = emptyList(),
    var status: TaskStatus = TaskStatus.PENDING,
    val reminder: Reminder = Reminder.NONE,
    val priority: Priority = Priority.NO_PRIORITY,
    val userEmail: String? = null
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
    DAY_BEFORE("1 day early");

    companion object {
        fun getFromTitle(title: String) = entries.toTypedArray()
            .find { reminder -> reminder.title == title } ?: NONE
    }
}

enum class Repeat(val title: String) {
    NONE("None"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly"),
    CUSTOM("Custom");

    companion object {
        fun getFromTitle(title: String) = Repeat.entries.toTypedArray()
            .find { reminder -> reminder.title == title } ?: NONE
    }
}

enum class Priority(val title: String, val color: Color) {
    HIGH("High", Color.Red),
    MEDIUM("Medium", Color(255, 165, 0)),
    LOW("Low", Color.Blue),
    NO_PRIORITY("No priority", Color.Black);

    companion object {
        fun getFromTitle(title: String) = Priority.entries.toTypedArray()
            .find { reminder -> reminder.title == title } ?: NO_PRIORITY
    }
}

