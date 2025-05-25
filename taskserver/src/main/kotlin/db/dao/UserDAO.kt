package com.example.db.dao

import com.example.db.tables.UsersTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID

class UserDAO(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, UserDAO>(UsersTable)

    var login by UsersTable.login
    var passwordHash by UsersTable.passwordHash
    var username by UsersTable.username
    var email by UsersTable.email
}