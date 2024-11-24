package com.project.lifeos.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.project.lifeos.data.Category
import com.project.lifeos.data.Goal
import com.project.lifeos.data.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class GoalScreenViewModel : ScreenModel {
    private val _uiState = MutableStateFlow<GoalsUIState>(GoalsUIState.NoGoals)
    val uiState = _uiState.asStateFlow()

    fun init() {
        screenModelScope.launch {
//            _uiState.emit(GoalsUIState.GoalsFounded(list))
        }
    }
}

sealed class GoalsUIState {
    data class GoalsFounded(val goals: List<Goal>) : GoalsUIState()
    data object NoGoals : GoalsUIState()
}