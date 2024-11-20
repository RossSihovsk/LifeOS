package com.project.lifeos.data

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import java.time.LocalDate

data class Goal(
    val id: Long? = null,
    val title: String,
    val shortPhrase: String,
    val category: Category,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val status: GoalStatus = GoalStatus.IN_PROGRESS,
    val tasksId: List<Long>
)

enum class Category(val color: Color, val title: String) {
    SPORT(Color(0xFFA1D4DE), "Sport"),
    STUDY(Color(0xFFE09E6F),"Study"),
    WORK(Color(0xFFBCBCBC),"Work"),
    PERSONAL(Color(0xFFE8DEF7), "Personal")
}

enum class GoalStatus {
    IN_PROGRESS,
    COMPLETED,
    ABANDONED
}