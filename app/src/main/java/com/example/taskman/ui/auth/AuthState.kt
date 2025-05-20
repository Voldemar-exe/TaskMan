package com.example.taskman.ui.auth

data class AuthState(
    val authMode: AuthMode = AuthMode.Register,
    val login: String = "",
    val email: String = "",
    val username: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean? = null
)
