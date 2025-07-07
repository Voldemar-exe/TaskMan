package com.example.data.repository

import com.example.datastore.HistoryDataSource
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class HistoryRepositoryImpl @Inject constructor(
    private val historyDataSource: HistoryDataSource
) : HistoryRepository {

    private val historyState = historyDataSource.getHistory().stateIn(
        scope = CoroutineScope(Dispatchers.Default),
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = emptyList()
    )

    override suspend fun addToHistory(query: String) {
        historyDataSource.addToHistory(query)
    }

    override suspend fun clearHistory() {
        historyDataSource.clearHistory()
    }

    override fun getHistory(): Flow<List<String>> = historyState
}
