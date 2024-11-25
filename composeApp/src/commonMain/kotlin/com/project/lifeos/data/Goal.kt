package com.project.lifeos.data

import androidx.compose.ui.graphics.Color
import java.time.LocalDate
import java.util.UUID

data class Goal(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val shortPhrase: String,
    val category: Category,
    val startDate: LocalDate = LocalDate.now(),
    val duration: Duration,
    val userMail: String?
)

enum class Category(val color: Color, val title: String) {
    SPORT(Color(0xFFA1D4DE), "Sport"),
    STUDY(Color(0xFFE09E6F), "Study"),
    WORK(Color(0xFFBCBCBC), "Work"),
    PERSONAL(Color(0xFFE8DEF7), "Personal")
}

enum class Duration(val title: String) {
    TWO_WEEKS("Two Weeks"),
    MONTH("One Month"),
    THREE_MONTH("Three Month"),
    SIX_MONTH("Six Month"),
    YEAR("One Year")
}

enum class GoalStatus {
    IN_PROGRESS,
    COMPLETED,
    ABANDONED
}