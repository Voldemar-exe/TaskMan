package com.example.taskman.ui.utils

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ThemeRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : ThemeRepository {

    companion object {
        private const val THEME_KEY = "is_dark_theme"
    }

    private val _themeState = MutableStateFlow(isDarkTheme())
    override fun getThemeFlow() = _themeState.asStateFlow()

    override fun saveTheme(isDarkTheme: Boolean) {
        sharedPreferences.edit { putBoolean(THEME_KEY, isDarkTheme) }
        _themeState.value = isDarkTheme
    }

    override fun isDarkTheme(): Boolean {
        return sharedPreferences.getBoolean(THEME_KEY, false)
    }
}