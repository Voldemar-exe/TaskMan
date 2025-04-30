package com.example.taskman.ui.search

sealed interface SearchIntent {
    data class ChangeSearchText(val text: String) : SearchIntent
    data object Search : SearchIntent
    data object ClearSearch : SearchIntent
    data object ClearSearchHistory : SearchIntent
}
