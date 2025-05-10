package com.example.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object GroupsTable : IntIdTable("groups") {
    val name = varchar("name", 255)
    val icon = integer("icon")
    val color = long("color")
}