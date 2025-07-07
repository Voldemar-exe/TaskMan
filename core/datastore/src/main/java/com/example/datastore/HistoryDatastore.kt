package com.example.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.historyDataStore by preferencesDataStore(
    name = "history_prefs"
)
private const val MAX_HISTORY_SIZE = 10

class HistoryDatastore @Inject constructor(context: Context): HistoryDataSource {

    private val HISTORY_KEY = stringSetPreferencesKey("history_search")
    private val dataStore = context.historyDataStore

    override suspend fun addToHistory(query: String) {
        dataStore.edit { prefs ->
            val history = (prefs[HISTORY_KEY] ?: emptySet()).toMutableSet().apply {
                remove(query)
                add(query)
            }

            prefs[HISTORY_KEY] = if (history.size > MAX_HISTORY_SIZE) {
                history.take(MAX_HISTORY_SIZE).toSet()
            } else {
                history
            }
        }
    }

    override fun getHistory(): Flow<List<String>> {
        return dataStore.data.map { prefs ->
            prefs[HISTORY_KEY]?.toList() ?: emptyList()
        }
    }

    override suspend fun clearHistory() {
        dataStore.edit { it[HISTORY_KEY] = emptySet() }
    }
}