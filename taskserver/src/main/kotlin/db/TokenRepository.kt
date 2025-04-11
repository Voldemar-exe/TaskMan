package com.example.db

interface TokenRepository {
    fun saveToken(login: String, token: String)
    fun validateToken(token: String): Boolean
}