package com.example.db

import com.example.auth.UserDto
import com.example.db.DatabaseFactory.suspendTransaction
import com.example.db.dao.UserDAO
import com.example.db.tables.UsersTable
import org.jetbrains.exposed.sql.insert

class UserRepositoryImpl : UserRepository {

    override suspend fun findByLogin(login: String): UserDto? = suspendTransaction {
        UserDAO.findById(login)?.let {
            UserDto(
                it.login.value,
                it.passwordHash,
                it.username,
                it.email
            )
        }
    }

    override suspend fun deleteByLogin(login: String): Boolean = suspendTransaction {
        UserDAO.findById(login)?.let {
            it.delete()
            true
        }
        false
    }

    override suspend fun createUser(user: UserDto) = suspendTransaction {
        UsersTable.insert {
            it[login] = user.login
            it[passwordHash] = user.passwordHash
            it[username] = user.username
            it[email] = user.email
        }.insertedCount > 0
    }
}