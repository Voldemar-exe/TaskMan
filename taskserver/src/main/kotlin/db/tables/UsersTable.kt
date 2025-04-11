package com.example.db.tables

import org.jetbrains.exposed.sql.Table

object UsersTable : Table("users") {
    val login = varchar("login", 50).autoIncrement()
    val passwordHash = varchar("passwordHash", 255)
    val username = varchar("username", 255).nullable()
    val email = varchar("email", 255).nullable()

    override val primaryKey: PrimaryKey?
        get() = PrimaryKey(login)
}