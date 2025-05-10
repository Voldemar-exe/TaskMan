package com.example.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object UserTaskTable : IntIdTable("user_task") {
    val login = reference("login", UsersTable)
    val taskId = reference("task_id", TasksTable)
}