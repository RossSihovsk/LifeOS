package com.project.lifeos.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.project.lifeos.data.Goal
import com.project.lifeos.data.Task
import com.project.lifeos.repository.GoalRepository
import com.project.lifeos.repository.TaskRepository
import com.project.lifeos.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import com.project.lifeos.utils.stringToDate
import java.time.temporal.WeekFields
import java.util.Locale

class GoalScreenViewModel(
    private val goalRepository: GoalRepository,
    private val userRepository: UserRepository,
    private val taskRepository: TaskRepository
) : ScreenModel {
    private val _uiState = MutableStateFlow<GoalsUIState>(GoalsUIState.NoGoals)
    val uiState = _uiState.asStateFlow()

    private val goalMappers = hashSetOf<GoalDataMapper>()

    fun init() {
        screenModelScope.launch(Dispatchers.IO) {
            val user = userRepository.getLastUser()
            val goals = goalRepository.getForUser(user?.mail)

            if (goals.isEmpty()) {
                _uiState.emit(GoalsUIState.NoGoals)
            } else {
                addToDataList(goals)
                _uiState.emit(GoalsUIState.GoalsFounded(goals))
            }
        }
    }

    private suspend fun addToDataList(goals: List<Goal>) {
        goals.forEach { goal: Goal ->
            val tasks = taskRepository.getTaskForGoal(goal.id)

            val tasksThisWeek = screenModelScope.async(Dispatchers.Default) { countDatesInCurrentWeek(tasks) }
            val percentageDone = screenModelScope.async(Dispatchers.Default) { countPercentageDone(tasks) }

            goalMappers.add(
                GoalDataMapper(
                    goal = goal,
                    tasksThisWeek = tasksThisWeek.await(),
                    percentageDone = percentageDone.await()
                )
            )
        }
    }

    private fun countPercentageDone(tasks: List<Task>): Int {
        val completedTasks = tasks.sumOf { task -> task.dateStatuses.count { it.status } }
        val totalTasks = tasks.sumOf { it.dateStatuses.size }

        return if (tasks.isNotEmpty()) {
            ((completedTasks.toDouble() / totalTasks) * 100).toInt()
        } else {
            0
        }
    }

    private fun countDatesInCurrentWeek(tasks: List<Task>): Int {
        val today = LocalDate.now()
        val weekFields = WeekFields.of(Locale.getDefault())

        val firstDayOfWeek = today.with(weekFields.dayOfWeek(), 1)
        val lastDayOfWeek = today.with(weekFields.dayOfWeek(), 7)

        var tasksNumber = 0

        tasks.forEach { task ->
            tasksNumber += task.dateStatuses.map { stringToDate(it.date) }
                ?.count { it?.isAfter(firstDayOfWeek.minusDays(1)) == true && it.isBefore(lastDayOfWeek.plusDays(1)) }
                ?: 0
        }

        return tasksNumber
    }

    fun percentageDone(goal: Goal): Int = goalMappers.first { goal == it.goal }.percentageDone

    fun tasksThisWeek(goal: Goal): Int = goalMappers.first { goal == it.goal }.tasksThisWeek

    fun deleteGoal(goalToDelete: Goal?) {
        goalRepository.deleteGoal(goalToDelete?.id!!)
        init()
    }
}

data class GoalDataMapper(
    val goal: Goal,
    val tasksThisWeek: Int,
    val percentageDone: Int
)

sealed class GoalsUIState {
    data class GoalsFounded(val goals: List<Goal>) : GoalsUIState()
    data object NoGoals : GoalsUIState()
}