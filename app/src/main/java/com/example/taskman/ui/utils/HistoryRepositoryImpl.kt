package com.example.taskman.ui.utils

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.serialization.json.Json

class HistoryRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : HistoryRepository {

    companion object {
        private const val HISTORY_KEY = "history"
        private const val MAX_HISTORY_SIZE = 10
        private val json = Json { ignoreUnknownKeys = true }
    }

    override fun getHistoryFlow(): Flow<List<String>> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == HISTORY_KEY) {
                trySend(getHistory())
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        awaitClose {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    override fun clearHistory() {
        sharedPreferences.edit { remove(HISTORY_KEY) }
    }

    override fun addToHistory(query: String) {
        val currentHistory = getHistory().toMutableList()

        currentHistory.remove(query)
        currentHistory.add(0, query)

        val jsonHistory = json.encodeToString(
            if (currentHistory.size > MAX_HISTORY_SIZE) {
                currentHistory.take(MAX_HISTORY_SIZE)
            } else {
                currentHistory
            }
        )

        sharedPreferences.edit { putString(HISTORY_KEY, jsonHistory) }
    }

    override fun getHistory(): List<String> {
        val jsonStr = sharedPreferences.getString(HISTORY_KEY, null)
        return if (jsonStr != null) {
            try {
                json.decodeFromString(jsonStr)
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }
}
