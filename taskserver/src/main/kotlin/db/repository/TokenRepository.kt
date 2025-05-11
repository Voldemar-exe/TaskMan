package com.example.db.repository

interface TokenRepository {
    fun saveToken(login: String, token: String)
    suspend fun getToken(login: String): String?
}