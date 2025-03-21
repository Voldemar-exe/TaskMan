package com.example.taskman.ui.auth

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(private val authService: AuthService) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthState())
    val uiState: StateFlow<AuthState> = _uiState.asStateFlow()

    fun processIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.UpdateLogin -> updateLogin(intent.login)
            is AuthIntent.UpdatePassword -> updatePassword(intent.password)
            is AuthIntent.UpdateConfirmPassword -> updateConfirmPassword(intent.confirmPassword)
            is AuthIntent.Submit -> submitForm()
            is AuthIntent.ToggleMode -> toggleMode()
        }
    }

    private fun updateLogin(login: String) {
        _uiState.update { currentState ->
            currentState.copy(
                login = login,
                error = null
            )
        }
    }

    private fun updatePassword(password: String) {
        _uiState.update { currentState ->
            currentState.copy(
                password = password,
                error = null
            )
        }
    }

    private fun updateConfirmPassword(confirmPassword: String) {
        _uiState.update { currentState ->
            currentState.copy(
                confirmPassword = confirmPassword,
                error = null
            )
        }
    }

    private fun submitForm() {
        val currentState = _uiState.value
        if (currentState.isRegister) {
            if (currentState.password != currentState.confirmPassword) {
                _uiState.update { it.copy(error = "Пароли не совпадают") }
                return
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            val success = if (currentState.isRegister) {
                authService.registerUser(currentState.login, currentState.password)
            } else {
                authService.loginUser(currentState.login, currentState.password)
            }

            if (success) {
                _uiState.update { it.copy(error = null) }
                // TODO Перейти к следующему экрану
            } else {
                _uiState.update { it.copy(error = "Ошибка при ${if (currentState.isRegister) "регистрации" else "входе"}") }
            }
        }
    }

    private fun toggleMode() {
        _uiState.update { currentState ->
            currentState.copy(
                isRegister = !currentState.isRegister,
                error = null
            )
        }
    }
}