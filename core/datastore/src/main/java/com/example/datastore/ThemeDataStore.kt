package com.example.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.themeDataStore by preferencesDataStore(
    name = "theme_prefs"
)

class ThemeDataStore @Inject constructor(context: Context): ThemeDataSource {

    private val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
    private val dataStore = context.themeDataStore

    override suspend fun saveTheme(isDarkTheme: Boolean) {
        dataStore.edit { prefs ->
            prefs[IS_DARK_THEME] = isDarkTheme
        }
    }

    override fun isDarkTheme(): Flow<Boolean> {
        return dataStore.data.map { prefs ->
            prefs[IS_DARK_THEME] ?: false
        }
    }
}