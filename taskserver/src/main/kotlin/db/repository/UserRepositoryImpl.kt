package com.example.db.repository

import com.example.db.DatabaseFactory.suspendTransaction
import com.example.db.dao.GroupTaskDAO
import com.example.db.dao.TokenDAO
import com.example.db.dao.UserDAO
import com.example.db.dao.UserGroupDAO
import com.example.db.dao.UserTaskDAO
import com.example.db.tables.GroupTaskTable
import com.example.db.tables.TokensTable
import com.example.db.tables.UserGroupTable
import com.example.db.tables.UserTaskTable
import com.example.model.UserDto

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

    override suspend fun deleteByLogin(login: String): Result<String> = suspendTransaction {
        val user = UserDAO.findById(login)

        if (user == null) return@suspendTransaction Result.failure(error("User not found"))
        deleteUserData(user.login.value)
        TokenDAO.find { TokensTable.login eq login }.forEach { it.delete() }
        user.delete()
        Result.success("User $login was deleted")
    }

    override suspend fun deleteDataByLogin(login: String): Result<String> = suspendTransaction {
        val user = UserDAO.findById(login)

        if (user == null) return@suspendTransaction Result.failure(error("User not found"))

        deleteUserData(user.login.value)

        Result.success("User $login data was deleted")
    }

    private fun deleteUserData(login: String) {
        UserGroupDAO.find { UserGroupTable.login eq login }.forEach {
            it.delete()
            GroupTaskDAO.find { GroupTaskTable.groupId eq it.groupId.id }.forEach {
                it.delete()
            }
            it.groupId.delete()
        }
        UserTaskDAO.find { UserTaskTable.login eq login }.forEach {
            it.delete()
            it.taskId.delete()
        }
    }
}
