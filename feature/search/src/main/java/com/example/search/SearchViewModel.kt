package com.example.search

import android.util.Log
import androidx.compose.ui.util.fastFilter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.HistoryRepository
import com.example.data.repository.TaskRepository
import com.example.shared.IntentResult
import com.example.shared.UserTask
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    companion object {
        private const val TAG = "SearchViewModel"
    }

    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    private val _allTasks = MutableStateFlow(emptyList<UserTask>())

    init {
        viewModelScope.launch {
            taskRepository.allTasksListFlow.collect { tasks ->
                _allTasks.value = tasks
                _state.update { currentState ->
                    val searchedTaskIds = currentState.searchedTasks.map { it.localId }.toSet()

                    currentState.copy(
                        searchedTasks =
                            _allTasks.value.fastFilter { it.localId in searchedTaskIds }
                    )
                }
            }
        }
        viewModelScope.launch {
            historyRepository.getHistory().collect { historyList ->
                _state.update {
                    it.copy(searchHistory = historyList)
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

            is SearchIntent.ToggleTaskCompletion -> toggleTaskCompletion(intent.task)
        }
    }

    fun performSearch(query: String) {
        if (query.isEmpty()) return
        try {
            _state.update { it.copy(isLoading = true, searchedTasks = emptyList()) }
            viewModelScope.launch {
                historyRepository.addToHistory(query)

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
                        searchedTasks = searchedTasks
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

    fun toggleTaskCompletion(task: UserTask) = viewModelScope.launch {
        taskRepository.toggleTaskCompletion(task)
    }
}
