package com.example.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object GroupTaskTable : IntIdTable("group_task") {
    val groupId = reference("group_id", GroupsTable)
    val taskId = reference("task_id", TasksTable)
}