package com.project.lifeos.repository

import com.project.lifeos.data.Category
import com.project.lifeos.data.Duration
import com.project.lifeos.data.Goal
import com.project.lifeos.repository.local.LocalGoalDataSource

class GoalRepository(private val localGoalDataSource: LocalGoalDataSource) {
    fun getForUser(userMail: String?): List<Goal> = localGoalDataSource.getForUser(userMail)
    fun saveGoal(goal: Goal) = localGoalDataSource.saveGoal(goal)
    fun deleteGoal(id: String) = localGoalDataSource.deleteGoal(id)
    fun updateGoal(id: String, title: String, description: String, duration: Duration, category: Category) =
        localGoalDataSource.updateGoal(id, title, description, category, duration)
}