package com.example.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object TasksTable : IntIdTable("tasks") {
    val name = varchar("name", 255)
    val icon = varchar("icon", 50)
    val color = long("color")
    val type = varchar("type", 50)
    val isComplete = bool("is_complete")
    val date = long("date")
}

