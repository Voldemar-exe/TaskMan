package com.example.db

import com.example.db.tables.GroupTaskTable
import com.example.db.tables.GroupsTable
import com.example.db.tables.TasksTable
import com.example.db.tables.TokensTable
import com.example.db.tables.UserGroupTable
import com.example.db.tables.UserTaskTable
import com.example.db.tables.UsersTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.Application
import io.ktor.server.application.log
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init(environment: Application) {
        Database.connect(hikari())
        environment.log.info("Connected to PostgreSQL via HikariCP")

        transaction {
            SchemaUtils.create(
                UsersTable,
                TokensTable,
                TasksTable,
                GroupsTable,
                GroupTaskTable,
                UserTaskTable,
                UserGroupTable
            )
        }
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            jdbcUrl = "jdbc:postgresql://localhost:5432/taskman"
            username = "task_server"
            password = "admin"
            maximumPoolSize = 10
            validate()
        }
        return HikariDataSource(config)
    }

    suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
        newSuspendedTransaction(Dispatchers.IO, statement = block)
}