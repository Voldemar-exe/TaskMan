package com.example.taskman.ui.auth

interface AuthStorage {
    suspend fun saveToken(token: String)
    suspend fun getToken(): String?
    suspend fun clearToken()
}