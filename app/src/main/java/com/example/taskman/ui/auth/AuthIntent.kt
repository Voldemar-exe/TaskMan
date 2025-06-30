package com.example.taskman.ui.auth

sealed interface AuthIntent {
    data object Submit : AuthIntent
    data object ToggleMode : AuthIntent
}
