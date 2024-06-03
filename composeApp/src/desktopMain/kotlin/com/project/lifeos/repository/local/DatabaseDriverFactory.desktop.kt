package com.project.lifeos.repository.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.lifeos.LifeOsDatabase
import java.io.File
import java.util.Properties
import java.util.logging.Logger

private val logger = co.touchlab.kermit.Logger.withTag("DatabaseDriverFactoryDesktop")

actual class DatabaseDriverFactory {
    actual fun create(): SqlDriver {

        val databasePath = File(System.getProperty("java.io.tmpdir"), "lifeos_database.db")
        logger.i("Path to file: ${databasePath.absolutePath}")
        val driver: SqlDriver =
            JdbcSqliteDriver(url = "jdbc:sqlite:${databasePath.absolutePath}") //C:\Users\Huawei\AppData\Local\Temp in my case
        LifeOsDatabase.Schema.create(driver)
        return driver

    }
}