package com.example.taskman.ui.auth

sealed interface AuthState {
    data object Loading : AuthState
    data class Content(
        val isRegister: Boolean = true,
        val login: String = "",
        val password: String = "",
        val confirmPassword: String = "",
        val error: String? = null
    ) : AuthState
    data class Success(val message: String) : AuthState
    data class Error(val errorMessage: String) : AuthState
}