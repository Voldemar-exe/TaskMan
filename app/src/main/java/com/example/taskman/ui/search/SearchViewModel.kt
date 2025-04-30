package com.example.taskman.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskman.db.TaskDao
import com.example.taskman.ui.components.IntentResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(
    private val taskDao: TaskDao
) : ViewModel() {
    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    fun onIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.ChangeSearchText ->
                _state.update { it.copy(searchText = intent.text) }

            SearchIntent.Search ->
                performSearch(_state.value.searchText)

            SearchIntent.ClearSearch ->
                _state.update { it.copy(searchText = "", searchedTasks = emptyList()) }

            SearchIntent.ClearSearchHistory ->
                _state.update { it.copy(searchHistory = emptyList()) }

        }
    }

    private fun performSearch(query: String) {
        if (query.isEmpty()) return

        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }
                val tasks = taskDao.getAllTasksList()
                _state.update { state ->
                    state.copy(
                        searchedTasks = tasks.filter { it.name.contains(query, true) },
                        result =
                            if (tasks.isEmpty()) IntentResult.None
                            else IntentResult.Success(SearchIntent.Search.toString()),
                        isLoading = false,
                        searchHistory =
                            if (!state.searchHistory.contains(query)) {
                                listOf(query) + state.searchHistory.take(9)
                            } else {
                                state.searchHistory
                            }
                    )
                }
            } catch (e: Exception) {
                _state.update { state ->
                    state.copy(
                        result = IntentResult.Error(e.message),
                        isLoading = false
                    )
                }
            }
        }
    }
}