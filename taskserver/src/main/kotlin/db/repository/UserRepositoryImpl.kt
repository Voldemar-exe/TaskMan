package com.example.db.repository

import com.example.db.DatabaseFactory.suspendTransaction
import com.example.db.dao.UserDAO
import com.example.shared.dto.UserDto

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
        UserDAO.new(user.login) {
            passwordHash = user.passwordHash
            username = user.username
            email = user.email
        }
    }
}