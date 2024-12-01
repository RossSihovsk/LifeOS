package com.project.lifeos.repository.local

import co.touchlab.kermit.Logger
import com.lifeos.GoalEntity
import com.lifeos.LifeOsDatabase
import com.project.lifeos.data.Category
import com.project.lifeos.data.Duration
import com.project.lifeos.data.Goal
import com.project.lifeos.utils.convertLocalDateToString
import com.project.lifeos.utils.stringToDate

class LocalGoalDataSource(db: LifeOsDatabase) {

    private val logger = Logger.withTag("LocalGoalDataSource")
    private val queries = db.goalEntityQueries

    fun getForUser(userMail: String?): List<Goal> {
        logger.d("getForUser $userMail")
        return queries.getForUser(userMail).executeAsList().mapToGoalList()
    }

    fun saveGoal(goal: Goal) {
        logger.d("saveGoal $goal")
        queries.addNewGoal(
            id = goal.id,
            name = goal.title,
            description = goal.shortPhrase,
            duration = goal.duration.name,
            category = goal.category.name,
            startDate = convertLocalDateToString(goal.startDate),
            userMail = goal.userMail
        )
    }

    fun deleteGoal(id: String) {
        logger.d("deleteGoal: $id")
        queries.deleteGoal(id)
    }

    fun updateGoal(id: String, title: String, description: String, category: Category, duration: Duration) {
        logger.d(
            "updateGoal - id: $id, title: $title, description: $description, " +
                    "category: $category, duration: $duration"
        )
        queries.updateGoal(
            name = title,
            description = description,
            duration = duration.name,
            category = category.name,
            id = id
        )
    }

    fun deleteAllForUser(userMail: String) {
        queries.deleteAllForUser(userMail)
    }
}

private fun List<GoalEntity>.mapToGoalList(): List<Goal> {
    val goalList = arrayListOf<Goal>().toMutableList()
    this.forEach { goalEntity ->
        val goal = Goal(
            id = goalEntity.id,
            title = goalEntity.name,
            shortPhrase = goalEntity.description,
            duration = Duration.valueOf(goalEntity.duration),
            category = Category.valueOf(goalEntity.category),
            startDate = stringToDate(goalEntity.startDate),
            userMail = goalEntity.userMail
        )
        goalList.add(goal)
    }

    return goalList
}