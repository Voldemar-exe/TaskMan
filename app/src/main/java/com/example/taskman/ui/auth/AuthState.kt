package com.example.taskman.ui.auth

import androidx.compose.foundation.text.input.TextFieldState

data class AuthState(
    val authMode: AuthMode = AuthMode.Register,
    val loginState: TextFieldState = TextFieldState(),
    val emailState: TextFieldState = TextFieldState(),
    val usernameState: TextFieldState = TextFieldState(),
    val passwordState: TextFieldState = TextFieldState(),
    val confirmPasswordState: TextFieldState = TextFieldState(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean? = null
)
