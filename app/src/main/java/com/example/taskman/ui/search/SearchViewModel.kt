package com.example.taskman.ui.search

import android.util.Log
import androidx.compose.ui.util.fastFilter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskman.db.TaskDao
import com.example.taskman.model.MyTask
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
    companion object {
        private const val TAG = "SearchViewModel"
    }

    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    private val _allTasks = MutableStateFlow(emptyList<MyTask>())

    val history: StateFlow<List<String>> = historyRepository.getHistoryFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = historyRepository.getHistory()
        )

    init {
        viewModelScope.launch {
            taskDao.getAllTasksFlow().collect {
                _allTasks.value = it
                _state.update { currentState ->
                    val searchedTaskIds = currentState.searchedTasks.map { it.taskId }.toSet()

                    currentState.copy(
                        searchedTasks = _allTasks.value.fastFilter { it.taskId in searchedTaskIds }
                    )
                }
            }
        }
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
                _state.update { it.copy(expandedSearch = intent.expanded) }
        }
    }

    fun performSearch(query: String) {
        if (query.isEmpty()) return
        try {
            _state.update { it.copy(isLoading = true, searchedTasks = emptyList()) }
            historyRepository.addToHistory(query)
            viewModelScope.launch {
                val allTasks = _allTasks.value
                val searchedTasks = allTasks.filter { it.name.contains(query, true) }

                _state.update { state ->
                    state.copy(
                        result =
                            if (searchedTasks.isEmpty()) {
                                IntentResult.None
                            } else {
                                IntentResult.Success(SearchIntent.Search.toString())
                            },
                        expandedSearch = false,
                        isLoading = false,
                        searchedTasks = searchedTasks,
                        searchHistory = history.value
                    )
                }
            }
        } catch (e: Exception) {
            _state.update { state ->
                state.copy(
                    expandedSearch = false,
                    result = IntentResult.Error(e.message),
                    isLoading = false
                )
            }
        }
    }
}
