package com.example.data

interface TokenProvider {
    suspend fun getToken(): String?
}