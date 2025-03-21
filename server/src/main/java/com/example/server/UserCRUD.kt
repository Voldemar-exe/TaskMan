package com.example.server

import at.favre.lib.crypto.bcrypt.BCrypt
import org.ktorm.dsl.QueryRowSet
import org.ktorm.dsl.delete
import org.ktorm.dsl.eq
import org.ktorm.dsl.from
import org.ktorm.dsl.insert
import org.ktorm.dsl.map
import org.ktorm.dsl.select
import org.ktorm.dsl.update
import org.ktorm.dsl.where

fun createUser(username: String, email: String, password: String): Int {
    val passwordHash = BCrypt.withDefaults().hashToString(12, password.toCharArray())
    return DatabaseFactory.database.insert(Users) {
        set(it.username, username)
        set(it.email, email)
        set(it.passwordHash, passwordHash)
    }
}

fun authenticateUser(username: String, password: String): Boolean {
    val user = DatabaseFactory.database.from(Users)
        .select()
        .where { Users.username eq username }
        .map { rowToUser(it) }
        .firstOrNull()

    return user != null && BCrypt
        .verifyer()
        .verify(password.toCharArray(), user.passwordHash)
        .verified
}

fun getUserById(id: Int): User? {
    return DatabaseFactory.database.from(Users)
        .select()
        .where { Users.id eq id }
        .map { rowToUser(it) }
        .firstOrNull()
}

fun getAllUsers(): List<User> {
    return DatabaseFactory.database.from(Users)
        .select()
        .map { rowToUser(it) }
}

fun updateUser(id: Int, username: String, email: String, passwordHash: String): Int {
    return DatabaseFactory.database.update(Users) {
        set(it.username, username)
        set(it.email, email)
        set(it.passwordHash, passwordHash)
        where { it.id eq id }
    }
}

fun deleteUser(id: Int): Int {
    return DatabaseFactory.database.delete(Users) { it.id eq id }
}

private fun rowToUser(row: QueryRowSet): User {
    return User(
        id = row[Users.id],
        username = row[Users.username]!!,
        email = row[Users.email]!!,
        passwordHash = row[Users.passwordHash]!!
    )
}