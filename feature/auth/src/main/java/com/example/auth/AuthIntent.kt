package com.example.auth

sealed interface AuthIntent {
    data object Submit : AuthIntent
    data object ToggleMode : AuthIntent
}
