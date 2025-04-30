package com.example.taskman.ui.search

import androidx.compose.runtime.Stable
import com.example.taskman.model.MyTask
import com.example.taskman.ui.components.IntentResult

@Stable
data class SearchState(
    val searchText: String = "",
    val result: IntentResult = IntentResult.None,
    val isLoading: Boolean = false,
    val searchedTasks: List<MyTask> = emptyList(),
    val searchHistory: List<String> = emptyList()
)
