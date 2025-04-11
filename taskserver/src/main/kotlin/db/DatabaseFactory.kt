package com.example.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.Application
import io.ktor.server.application.log
import org.jetbrains.exposed.sql.Database

object DatabaseFactory {
    fun init(environment: Application) {
        val config = HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            jdbcUrl = "jdbc:postgresql://localhost:5432/taskman"
            username = "task_server"
            password = "admin"
            maximumPoolSize = 10
            validate()
        }

        val dataSource = HikariDataSource(config)
        Database.connect(dataSource)
        environment.log.info("Connected to PostgreSQL via HikariCP")
    }
}