package com.example.datastore

import kotlinx.coroutines.flow.Flow

interface ThemeDataSource {
    suspend fun saveTheme(isDarkTheme: Boolean)
    fun isDarkTheme(): Flow<Boolean>
}