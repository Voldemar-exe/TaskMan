package com.example.search

import com.example.shared.UserTask

sealed interface SearchIntent {
    data class ChangeInputText(val text: String) : SearchIntent
    data object Search : SearchIntent
    data class OnExpandedChange(val expanded: Boolean) : SearchIntent
    data object ClearSearchQuery : SearchIntent
    data object ClearSearchHistory : SearchIntent
    data class ToggleTaskCompletion(val task: UserTask) : SearchIntent
}
