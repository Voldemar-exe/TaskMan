package com.example.taskman.ui.utils

import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun addToHistory(query: String)
    fun clearHistory()
    fun getHistory(): List<String>
    fun getHistoryFlow(): Flow<List<String>>
}
