package com.project.lifeos.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.project.lifeos.data.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TaskViewModel : ScreenModel {

    // Flow of tasks for a specific date
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())

    // Local mutable list of tasks
    val task = _tasks.asStateFlow()

    fun addTask(task: Task) {
        val currentTasks = _tasks.value.toMutableList()
        currentTasks.add(task)
        screenModelScope.launch {
            _tasks.emit(currentTasks)
        }
    }
}