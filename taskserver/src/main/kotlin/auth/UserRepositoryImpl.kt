package com.example.auth

import com.example.db.UserRepository
import com.example.db.tables.UsersTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepositoryImpl : UserRepository {

    override fun findByLogin(login: String): User? {
        return transaction {
            UsersTable.select { UsersTable.login eq login }
                .map {
                    User(
                        login = it[UsersTable.login],
                        passwordHash = it[UsersTable.passwordHash],
                        username = it[UsersTable.username] ?: "",
                        email = it[UsersTable.email] ?: ""
                    )
                }
                .singleOrNull()
        }
    }

    override fun deleteByLogin(login: String): Boolean {
        // TODO Delete users for tests
        return false
    }

    override fun createUser(
        login: String,
        passwordHash: String,
        username: String?,
        email: String?
    ) {
        transaction {
            UsersTable.insert {
                it[UsersTable.login] = login
                it[UsersTable.passwordHash] = passwordHash
                it[UsersTable.username] = username
                it[UsersTable.email] = email
            }
        }
    }
}