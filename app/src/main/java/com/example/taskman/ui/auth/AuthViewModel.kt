package com.example.taskman.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.request.LoginRequest
import com.example.shared.request.RegisterRequest
import com.example.taskman.api.auth.AuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authService: AuthService
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthState())
    val uiState: StateFlow<AuthState> = _uiState.asStateFlow()

    companion object {
        private const val TAG = "AuthViewModel"
    }

    fun onIntent(intent: AuthIntent) {
        Log.i(TAG, "$intent")
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
        val state = _uiState.value

        if (state.isRegister) {
            if (state.password != state.confirmPassword) {
                _uiState.update { it.copy(error = "Пароли не совпадают") }
                return
            }
        }

        viewModelScope.launch {
            val result = runCatching {
                if (state.isRegister) register(state.login, state.password)
                else login(state.login, state.password)
            }
            _uiState.update { current ->
                when {
                    result.isFailure -> {
                        Log.e("Auth", "Network error", result.exceptionOrNull())
                        current.copy(
                            error = "Сетевая ошибка. Попробуйте ещё раз."
                        )
                    }

                    result.getOrNull().isNullOrBlank() -> {
                        current.copy(
                            error =
                                if (state.isRegister) "Ошибка регистрации"
                                else "Ошибка входа"
                        )
                    }

                    else -> {
                        current.copy(
                            error = null,
                            success = true
                        )
                    }
                }
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

    // TODO ADD USERNAME AND EMAIL
    private suspend fun register(login: String, password: String): String? =
        authService.registerUser(RegisterRequest(login, password, "", ""))

    private suspend fun login(login: String, password: String): String? =
        authService.loginUser(LoginRequest(login, password))
}