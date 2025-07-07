package com.example.data.repository

import com.example.datastore.ThemeDataSource
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class ThemeRepositoryImpl @Inject constructor(
    private val themeDataSource: ThemeDataSource
) : ThemeRepository {

    private val themeState = themeDataSource.isDarkTheme().stateIn(
        scope = CoroutineScope(Dispatchers.Default),
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = false
    )

    override suspend fun saveTheme(isDarkTheme: Boolean) {
        themeDataSource.saveTheme(isDarkTheme)
    }

    override fun isDarkTheme(): Flow<Boolean> = themeState
}