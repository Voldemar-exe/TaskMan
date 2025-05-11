package com.example.db.repository

import com.example.shared.dto.UserDto


interface UserRepository {
    suspend fun createUser(user: UserDto): Boolean

    suspend fun findByLogin(login: String): UserDto?

    suspend fun deleteByLogin(login: String): Boolean
}