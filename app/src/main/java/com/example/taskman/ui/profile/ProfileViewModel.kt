package com.example.taskman.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskman.ui.auth.AuthStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authStorage: AuthStorage
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileState())
    val uiState: StateFlow<ProfileState> = _uiState.asStateFlow()

    init {
        _uiState.update {
            it.copy(userName = "Я", isInfo = false)
        }
    }

    fun onIntent(intent: ProfileIntent) {
        when (intent) {
            ProfileIntent.InfoClick -> _uiState.update { it.copy(isInfo = !it.isInfo) }
            ProfileIntent.ClearProfile -> viewModelScope.launch {
                authStorage.clearProfile()
            }
        }
    }
}