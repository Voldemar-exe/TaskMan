package com.example.db.tables

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column

object UsersTable : IdTable<String>("users") {
    val login = varchar("login", 50).entityId()
    val passwordHash = varchar("passwordHash", 255)
    val username = varchar("username", 255).nullable()
    val email = varchar("email", 255).nullable()

    override val primaryKey: PrimaryKey?
        get() = PrimaryKey(login)
    override val id: Column<EntityID<String>>
        get() = login
}

class UserDAO(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, UserDAO>(UsersTable)

    var login by UsersTable.login
    var passwordHash by UsersTable.passwordHash
    var username by UsersTable.username
    var email by UsersTable.email
}