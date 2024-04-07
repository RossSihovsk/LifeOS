package com.project.lifeos.repository.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.lifeos.LifeOsDatabase
import java.util.Properties

actual class DatabaseDriverFactory {
    actual fun create(): SqlDriver {
        val driver: SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        LifeOsDatabase.Schema.create(driver)
        return driver
    }
}