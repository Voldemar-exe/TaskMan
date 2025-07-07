package com.example.search

import androidx.compose.runtime.Stable
import com.example.shared.IntentResult
import com.example.shared.UserTask

@Stable
data class SearchState(
    val inputText: String = "",
    val result: IntentResult = IntentResult.None,
    val isLoading: Boolean = false,
    val expandedSearch: Boolean = false,
    val searchedTasks: List<UserTask> = emptyList(),
    val searchHistory: List<String> = emptyList()
)
