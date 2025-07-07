package com.example.data.repository

import kotlinx.coroutines.flow.Flow

interface ThemeRepository {
    suspend fun saveTheme(isDarkTheme: Boolean)
    fun isDarkTheme(): Flow<Boolean>
}
