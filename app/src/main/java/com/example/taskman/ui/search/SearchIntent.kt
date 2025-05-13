package com.example.taskman.ui.search

sealed interface SearchIntent {
    data class ChangeInputText(val text: String) : SearchIntent
    data object Search : SearchIntent
    data class OnExpandedChange(val expanded: Boolean) : SearchIntent
    data object ClearSearchQuery : SearchIntent
    data object ClearSearchHistory : SearchIntent
}
