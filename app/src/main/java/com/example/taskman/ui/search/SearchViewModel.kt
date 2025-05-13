package com.example.taskman.ui.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskman.db.TaskDao
import com.example.taskman.ui.components.IntentResult
import com.example.taskman.ui.utils.HistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(
    private val taskDao: TaskDao,
    private val historyRepository: HistoryRepository
) : ViewModel() {
    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    val history: StateFlow<List<String>> = historyRepository.getHistoryFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = historyRepository.getHistory()
        )

    companion object {
        private const val TAG = "SearchViewModel"
    }

    fun onIntent(intent: SearchIntent) {
        Log.i(TAG, "$intent")

        when (intent) {
            is SearchIntent.ChangeInputText ->
                _state.update { it.copy(inputText = intent.text) }

            SearchIntent.Search ->
                performSearch(_state.value.inputText)

            SearchIntent.ClearSearchQuery ->
                _state.update { it.copy(inputText = "", searchedTasks = emptyList()) }

            SearchIntent.ClearSearchHistory -> viewModelScope.launch {
                historyRepository.clearHistory()
            }

            is SearchIntent.OnExpandedChange ->
                _state.update { it.copy(expandedTaskList = intent.expanded) }
        }
    }

    fun performSearch(query: String) {
        if (query.isEmpty()) return
        try {
            _state.update { it.copy(isLoading = true) }
            historyRepository.addToHistory(query)
            _state.update { state ->
                state.copy(
                    result = IntentResult.Success(SearchIntent.Search.toString()),
                    expandedTaskList = false,
                    isLoading = false,
                    searchHistory = history.value
                )
            }
        } catch (e: Exception) {
            _state.update { state ->
                state.copy(
                    expandedTaskList = false,
                    result = IntentResult.Error(e.message),
                    isLoading = false
                )
            }
        }
    }
}
