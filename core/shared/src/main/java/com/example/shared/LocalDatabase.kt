package com.example.shared

interface LocalDatabase {
    suspend fun clearDb()
}