package com.example.db

import com.example.auth.User


interface UserRepository {
    fun createUser(
        login: String,
        passwordHash: String,
        username: String?,
        email: String?
    )

    fun findByLogin(login: String): User?

    fun deleteByLogin(login: String): Boolean?
}