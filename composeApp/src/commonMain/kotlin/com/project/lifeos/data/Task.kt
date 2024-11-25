package com.project.lifeos.data

import androidx.compose.ui.graphics.Color
import java.time.Duration

data class Task(
    val id: Long? = null,
    val title: String,
    val description: String? = null,
    val time: Long?,
    val dates: List<String>?,
    val checkItems: List<String> = emptyList(),
    var status: TaskStatus = TaskStatus.PENDING,
    val reminder: Reminder = Reminder.NONE,
    val priority: Priority = Priority.NO_PRIORITY,
    val userEmail: String? = null,
    val goalId: String? = null
) {
    fun withGoalId(newGoalId: String?): Task {
        return this.copy(goalId = newGoalId)
    }
}

enum class TaskStatus {
    PENDING,
    DONE,
    SKIPPED
}

enum class Reminder(val title: String, val duration: Duration) {
    NONE("None", Duration.ZERO),
    ON_TIME("On time", Duration.ZERO),
    FIVE_MIN_BEFORE("5 minutes early", Duration.ofMinutes(5)),
    THIRTY_MIN_BEFORE("30 minutes early", Duration.ofMinutes(30)),
    HOUR_BEFORE("1 hour early", Duration.ofHours(1)),
    DAY_BEFORE("1 day early", Duration.ofDays(1));

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

