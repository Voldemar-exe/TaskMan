package com.example.taskman.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AuthViewModel : ViewModel() {

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
                login = login
            )
        }
    }

    private fun updatePassword(password: String) {
        _uiState.update { currentState ->
            currentState.copy(
                password = password
            )
        }
    }

    private fun updateConfirmPassword(confirmPassword: String) {
        _uiState.update { currentState ->
            currentState.copy(
                confirmPassword = confirmPassword
            )
        }
    }

    private fun submitForm() {
        Log.i("SUBMIT_REG", "submitForm is called")
        /*viewModelScope.launch {

        }*/
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