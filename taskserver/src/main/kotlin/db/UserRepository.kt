package com.example.db

import com.example.auth.UserDto


interface UserRepository {
    suspend fun createUser(user: UserDto): Boolean

    suspend fun findByLogin(login: String): UserDto?

    suspend fun deleteByLogin(login: String): Boolean
}