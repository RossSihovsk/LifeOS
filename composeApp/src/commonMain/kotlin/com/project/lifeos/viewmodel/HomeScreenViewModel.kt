package com.project.lifeos.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import co.touchlab.kermit.Logger
import com.project.lifeos.data.DateStatus
import com.project.lifeos.data.Priority
import com.project.lifeos.data.Reminder
import com.project.lifeos.data.Task
import com.project.lifeos.repository.TaskRepository
import com.project.lifeos.repository.UserRepository
import com.project.lifeos.ui.view.calendar.CalendarDataSource
import com.project.lifeos.ui.view.calendar.CalendarUiModel
import com.project.lifeos.utils.convertLocalDateToString
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HomeScreenViewModel(
    private val calendarDataSource: CalendarDataSource,
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository

) : ScreenModel {
    private val logger = Logger.withTag("HomeScreenViewModel")
    var calendarUiModel by mutableStateOf(calendarDataSource.getData(lastSelectedDate = calendarDataSource.today))
        private set

    private val currentDate: String
        get() = convertLocalDateToString(calendarUiModel.selectedDate.date)

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun onDateClicked(date: CalendarUiModel.Date) {
        calendarUiModel = calendarUiModel.copy(
            selectedDate = date.copy(isSelected = true),
            visibleDates = calendarUiModel.visibleDates.map {
                it.copy(
                    isSelected = it.date.isEqual(date.date)
                )
            }
        )

        updateTasksState()

        logger.d("Selected date changed to $date")
    }

    fun onTaskStatusChanged(status: Boolean, task: Task) {
        logger.i("Task $task finished $status")
        val newStatus = task.dateStatuses.first { it.date.contains(currentDate) }.copy(status = !status)

        logger.d("newStatus: $newStatus")
        val updateJob = screenModelScope.async(Dispatchers.IO) {
            taskRepository.onTaskStatusChanged(newStatus, task)
        }

        updateTasksState(updateJob)
    }

    private fun separateTasks(tasks: List<Task>): Pair<List<Task>, List<Task>> {
        val completedTasks =
            tasks.filter { task -> task.dateStatuses.any { it.date.contains(currentDate) && it.status } }
        val uncompletedTasks =
            tasks.filter { task -> task.dateStatuses.any { it.date.contains(currentDate) && !it.status } }
        return Pair(completedTasks, uncompletedTasks)
    }

    private fun updateTasksState(changeJob: Deferred<Unit>? = null) = screenModelScope.launch(context = Dispatchers.IO){
        changeJob?.onAwait
        val updatedList = taskRepository.getTasksForDay(
            day = currentDate, userMail = userRepository.getLastUser()?.mail
        )
        if (updatedList.isEmpty()) {
            logger.d("No tasks for this day")
            _uiState.emit(HomeUiState.NoTaskForSelectedDate)
            return@launch
        }

        val (completed, uncompleted) = separateTasks(updatedList)
        _uiState.value =
            HomeUiState.TaskUpdated(completedTasks = completed, unCompletedTasks = uncompleted, currentDate)
    }

    fun init() {
        logger.i("Init")

        screenModelScope.launch(context = Dispatchers.IO) {
            taskRepository.tasksFlow.collect { tasks ->
                logger.d("tasksFlow collect")
                if (tasks.isEmpty()) _uiState.emit(HomeUiState.NoTaskForSelectedDate)
                else {
                    val (completed, uncompleted) = separateTasks(tasks)
                    _uiState.value =
                        HomeUiState.TaskUpdated(completedTasks = completed, unCompletedTasks = uncompleted, currentDate)
                }
            }
        }

        _uiState.value = HomeUiState.Loading
        val date = currentDate // Should be done on UI thread

        screenModelScope.launch(context = Dispatchers.IO) {
            val tasks = taskRepository.getTasksForDay(day = date, userMail = userRepository.getLastUser()?.mail)
            withContext(Dispatchers.Default) {
                if (tasks.isEmpty()) {
                    logger.i("Tasks are not present for $date")
                    _uiState.value = HomeUiState.NoTaskForSelectedDate

                } else {
                    logger.i("${tasks.size} Tasks are present for $date")
                    val (completed, uncompleted) = separateTasks(tasks)
                    _uiState.value =
                        HomeUiState.TaskUpdated(completedTasks = completed, unCompletedTasks = uncompleted, currentDate)
                }
            }
        }
    }

    fun deleteForToday(task: Task?) {
        logger.i("deleteForToday $task")
        screenModelScope.launch(Dispatchers.IO) {
            task?.let { taskToDelete ->
                val updateJob = screenModelScope.async(Dispatchers.IO) {
                    val updatedDates = task.dateStatuses.toMutableList().apply {
                        removeIf { it.date.contains(currentDate) }
                    }
                    taskRepository.updateTaskDates(updatedDates, taskToDelete.id!!)
                }

                updateTasksState(updateJob)
            }
        }
    }

    fun deleteCompletely(task: Task?) {
        logger.i("deleteCompletely $task")
        screenModelScope.launch(Dispatchers.IO) {
            task?.let { taskToDelete ->
                val updateJob = screenModelScope.async(Dispatchers.IO) {
                    taskRepository.deleteCompletely(taskToDelete.id!!)
                }

                updateTasksState(updateJob)
            }
        }
    }

    fun updateTask(
        id: Long?,
        title: String,
        description: String?,
        time: Long?,
        dates: List<DateStatus>,
        checkItems: List<String>,
        reminder: Reminder,
        priority: Priority
    ) {
        id?.let { taskId ->
            logger.i(
                "updateTask - id: $taskId, title: $title, description: $description, time: $time, " +
                        "dates: ${dates.joinToString { it.toString() }}, " +
                        "checkItems: ${checkItems.joinToString()}, reminder: $reminder, priority: $priority"
            )

            screenModelScope.launch(Dispatchers.IO) {
                val updateJob = screenModelScope.async(Dispatchers.IO) {
                    taskRepository.updateTask(taskId, title, description, time, dates, checkItems, reminder, priority)
                }

                updateTasksState(updateJob)

            }
        }
    }
}

sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class TaskUpdated(val completedTasks: List<Task>, val unCompletedTasks: List<Task>, val currentDate: String) :
        HomeUiState()

    data object NoTaskForSelectedDate : HomeUiState()
}
