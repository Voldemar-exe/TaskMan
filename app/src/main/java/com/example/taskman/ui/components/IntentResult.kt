package com.example.taskman.ui.components

sealed interface IntentResult {
    data object None : IntentResult
    data class Success(val message: String) : IntentResult
    data class Error(val message: String?) : IntentResult
}