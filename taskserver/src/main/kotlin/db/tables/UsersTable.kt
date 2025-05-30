package com.example.db.tables

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column

object UsersTable : IdTable<String>("users") {
    val login = varchar("login", 50).entityId()
    val passwordHash = varchar("passwordHash", 255)
    val username = varchar("username", 255)
    val email = varchar("email", 255)

    override val primaryKey = PrimaryKey(login)
    override val id: Column<EntityID<String>> = login
}

