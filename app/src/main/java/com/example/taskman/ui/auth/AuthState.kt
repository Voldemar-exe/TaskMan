package com.example.taskman.ui.auth

data class AuthState(
    val isRegister: Boolean = false,
    val login: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val error: String? = null
)