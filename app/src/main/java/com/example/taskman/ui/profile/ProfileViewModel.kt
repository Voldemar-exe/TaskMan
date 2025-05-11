package com.example.taskman.ui.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskman.ui.utils.SessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val sessionRepository: SessionRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileState())
    val uiState: StateFlow<ProfileState> = _uiState.asStateFlow()

    companion object {
        private const val TAG = "ProfileViewModel"
    }

    init {
        _uiState.update {
            it.copy(userName = "Я", isInfo = false)
        }
    }

    fun onIntent(intent: ProfileIntent) {
        Log.i(TAG, "$intent")
        when (intent) {
            ProfileIntent.InfoClick -> _uiState.update { it.copy(isInfo = !it.isInfo) }
            ProfileIntent.ClearProfile -> viewModelScope.launch {
                sessionRepository.clearSession()
            }
        }
    }
}