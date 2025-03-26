package com.example.taskman.ui.auth

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileState())
    val uiState: StateFlow<ProfileState> = _uiState.asStateFlow()

    init {
        _uiState.update {
            it.copy(userName = "Я", isInfo = false)
        }
    }

    fun processIntent(intent: ProfileIntent) {
        when (intent) {
            ProfileIntent.InfoClick -> _uiState.update { it.copy(isInfo = !it.isInfo) }
        }
    }
}