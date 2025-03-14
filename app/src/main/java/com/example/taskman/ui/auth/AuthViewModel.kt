package com.example.taskman.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val _state = MutableStateFlow<AuthState>(AuthState.Content())
    val state get() = _state.asStateFlow()

    fun processIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.UpdateLogin -> updateLogin(intent.login)
            is AuthIntent.UpdatePassword -> updatePassword(intent.password)
            is AuthIntent.UpdateConfirmPassword -> updateConfirmPassword(intent.confirmPassword)
            is AuthIntent.Submit -> submitForm()
            is AuthIntent.ToggleMode -> toggleMode()
            is AuthIntent.Back -> handleBack()
        }
    }

    private fun updateLogin(login: String) {
        val currentState = _state.value as? AuthState.Content ?: return
        _state.value = currentState.copy(login = login)
    }

    private fun updatePassword(password: String) {
        val currentState = _state.value as? AuthState.Content ?: return
        _state.value = currentState.copy(password = password)
    }

    private fun updateConfirmPassword(confirmPassword: String) {
        val currentState = _state.value as? AuthState.Content ?: return
        _state.value = currentState.copy(confirmPassword = confirmPassword)
    }

    private fun submitForm() {
        viewModelScope.launch {
            val currentState = _state.value as? AuthState.Content ?: return@launch
            if (currentState.isRegister && currentState.password != currentState.confirmPassword) {
                _state.value = currentState.copy(error = "Пароли не совпадают")
                return@launch
            }
            if (currentState.login.isEmpty() || currentState.password.isEmpty()) {
                _state.value = currentState.copy(error = "Заполните все поля")
                return@launch
            }
            _state.value = AuthState.Success(
                if (currentState.isRegister) "Регистрация успешна"
                else "Вход выполнен"
            )
        }
    }

    private fun toggleMode() {
        val currentState = _state.value as? AuthState.Content ?: return
        _state.value = currentState.copy(isRegister = !currentState.isRegister, error = null)
    }

    private fun handleBack() {
        _state.value = AuthState.Content()
    }
}