package com.project.lifeos.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import co.touchlab.kermit.Logger
import com.project.lifeos.data.Task
import com.project.lifeos.data.TaskStatus
import com.project.lifeos.di.AppModule
import com.project.lifeos.ui.view.calendar.CalendarUiModel
import com.project.lifeos.utils.convertLocalDateTimeToLong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HomeScreenViewModel : ScreenModel {
    private val logger = Logger.withTag("HomeScreenViewModel")
    private val calendarDataSource = AppModule.calendarDataSource
    private val repository = AppModule.taskRepository

    var calendarUiModel by mutableStateOf(calendarDataSource.getData(lastSelectedDate = calendarDataSource.today))
        private set

    private val currentDate: Long
        get() = convertLocalDateTimeToLong(calendarUiModel.selectedDate.date.atStartOfDay())

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        _uiState.value = HomeUiState.Loading
        val date = calendarUiModel.selectedDate.date.atStartOfDay() // Should be done on UI thread

        screenModelScope.launch(context = Dispatchers.IO) {
            logger.i("Init")

            val tasks = repository.getTasksForDay(convertLocalDateTimeToLong(date))
            withContext(Dispatchers.Main) {
                if (tasks.isEmpty()) {
                    logger.i("Tasks are not present for ${date.dayOfMonth} ${date.month.name}")
                    _uiState.value = HomeUiState.NoTaskForSelectedDate

                } else {
                    logger.i("${tasks.size} Tasks are present for ${date.dayOfMonth} ${date.month.name}")
                    val (completed, uncompleted) = separateTasks(tasks)
                    _uiState.value = HomeUiState.TaskUpdated(completedTasks = completed, unCompletedTasks = uncompleted)
                }
            }
        }
    }

    fun onDateClicked(date: CalendarUiModel.Date) {
        calendarUiModel = calendarUiModel.copy(
            selectedDate = date,
            visibleDates = calendarUiModel.visibleDates.map {
                it.copy(
                    isSelected = it.date.isEqual(date.date)
                )
            }
        )
    }

    fun onTaskStatusChanged(status: Boolean, task: Task) {
        logger.i("Task $task finished $status")
        if (status && task.status != TaskStatus.PENDING) return

        val updateJob = screenModelScope.async {
            repository.onTaskStatusChanged(status, task)
        }

        screenModelScope.launch {
            updateJob.onAwait
            val updatedList = repository.getTasksForDay(currentDate)
            val (completed, uncompleted) = separateTasks(updatedList)
            _uiState.value = HomeUiState.TaskUpdated(completedTasks = completed, unCompletedTasks = uncompleted)
        }
    }

    private fun separateTasks(tasks: List<Task>): Pair<List<Task>, List<Task>> {
        val completedTasks = tasks.filter { it.status == TaskStatus.DONE }
        val uncompletedTasks = tasks.filter { it.status == TaskStatus.PENDING }
        return Pair(completedTasks, uncompletedTasks)
    }
}

sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class TaskUpdated(val completedTasks: List<Task>, val unCompletedTasks: List<Task>) : HomeUiState()
    data object NoTaskForSelectedDate : HomeUiState()
}
