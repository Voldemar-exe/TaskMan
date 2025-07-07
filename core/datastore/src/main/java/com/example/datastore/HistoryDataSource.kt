package com.example.datastore

import kotlinx.coroutines.flow.Flow

interface HistoryDataSource {
    fun getHistory(): Flow<List<String>>
    suspend fun addToHistory(query: String)
    suspend fun clearHistory()
}