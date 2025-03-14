package com.example.taskman.ui.auth

sealed interface AuthIntent {
    data class UpdateLogin(val login: String) : AuthIntent
    data class UpdatePassword(val password: String) : AuthIntent
    data class UpdateConfirmPassword(val confirmPassword: String) : AuthIntent
    data object Submit : AuthIntent
    data object ToggleMode : AuthIntent
    data object Back : AuthIntent
}