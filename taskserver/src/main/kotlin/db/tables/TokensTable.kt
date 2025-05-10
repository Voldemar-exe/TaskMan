package com.example.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object TokensTable : IntIdTable("tokens") {
    val login = reference("login", UsersTable)
    val token = varchar("token", 255)
}


