package com.example.db

interface TokenRepository {
    fun saveToken(login: String, token: String)
    suspend fun getToken(login: String): String?
}