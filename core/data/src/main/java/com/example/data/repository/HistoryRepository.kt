package com.example.data.repository

import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    suspend fun addToHistory(query: String)
    suspend fun clearHistory()
    fun getHistory(): Flow<List<String>>
}
