package com.example.db.repository

import com.example.db.DatabaseFactory.suspendTransaction
import com.example.db.dao.TokenDAO
import com.example.db.dao.UserDAO
import com.example.db.dao.UserGroupDAO
import com.example.db.dao.UserTaskDAO
import com.example.db.tables.TokensTable
import com.example.db.tables.UserGroupTable
import com.example.db.tables.UserTaskTable
import com.example.shared.dto.UserDto

class UserRepositoryImpl : UserRepository {

    override suspend fun createUser(user: UserDto) = suspendTransaction {
        UserDAO.new(user.login) {
            passwordHash = user.passwordHash
            username = user.username
            email = user.email
        }
    }

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
        val user = UserDAO.findById(login)

        if (user == null) return@suspendTransaction false

        user.delete()
        true
    }

    override suspend fun deleteDataByLogin(login: String): Boolean = suspendTransaction {
        val user = UserDAO.findById(login)

        if (user == null) return@suspendTransaction false

        UserGroupDAO.find { UserGroupTable.login eq login }.forEach { it.delete() }
        UserTaskDAO.find { UserTaskTable.login eq login }.forEach { it.delete() }
        TokenDAO.find { TokensTable.login eq login }.forEach { it.delete() }

        true
    }
}
