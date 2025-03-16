package com.example.taskman.ui.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class ThemeRepositoryImpl(context: Context) : ThemeRepository {
    private val sharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    private val themeKey = "is_dark_theme"

    override fun saveTheme(isDarkTheme: Boolean) {
        sharedPreferences.edit { putBoolean(themeKey, isDarkTheme) }
    }

    override fun isDarkTheme(): Boolean {
        return sharedPreferences.getBoolean(themeKey, false)
    }

    override fun getThemeFlow(): Flow<Boolean> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == themeKey) {
                trySend(isDarkTheme())
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        awaitClose {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }
}