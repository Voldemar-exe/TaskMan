package com.example.taskman.ui.utils

import kotlinx.coroutines.flow.Flow

interface ThemeRepository {
    fun saveTheme(isDarkTheme: Boolean)
    fun isDarkTheme(): Boolean
    fun getThemeFlow(): Flow<Boolean>
}
