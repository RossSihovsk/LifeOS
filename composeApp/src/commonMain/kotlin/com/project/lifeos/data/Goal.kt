package com.project.lifeos.data

import java.time.LocalDate

data class Goal(
    val id: String,
    val title: String,
    val description: String? = null,
    val icon: ActivityIcon, // Can be an enum representing different goal types
    val startDate: LocalDate,
    val endDate: LocalDate,
    val status: GoalStatus = GoalStatus.IN_PROGRESS, // Goal status: IN_PROGRESS, COMPLETED, ABANDONED
    val tasks: List<Task> = emptyList() // List of associated tasks
)

enum class GoalStatus {
    IN_PROGRESS,
    COMPLETED,
    ABANDONED
}