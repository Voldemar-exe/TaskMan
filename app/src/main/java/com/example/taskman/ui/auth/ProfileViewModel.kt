package com.example.taskman.ui.auth

import androidx.lifecycle.ViewModel
import com.example.taskman.model.MyOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val uiState: StateFlow<ProfileState> = _uiState.asStateFlow()

    init {
        loadInitialTheme()
    }

    private fun loadInitialTheme() {
        _uiState.update {
            ProfileState.Content(
                userName = "Я",
                options = listOf(MyOption(name = "Темная тема", isActive = false)),
                isInfo = false
            )
        }
    }

    fun processIntent(intent: ProfileIntent) {
        _uiState.update { state ->
            when (state) {
                is ProfileState.Content -> handleContentState(state, intent)
                else -> state
            }
        }
    }

    private fun handleContentState(
        currentState: ProfileState.Content,
        intent: ProfileIntent
    ): ProfileState.Content {
        return when (intent) {
            ProfileIntent.InfoClick -> currentState.copy(isInfo = !currentState.isInfo)
            is ProfileIntent.OptionClick -> {
                val updatedOptions = currentState.options.map { option ->
                    if (option.name == intent.option.name) {
                        option.copy(isActive = !option.isActive)
                    } else {
                        option
                    }
                }

                currentState.copy(
                    options = updatedOptions
                )
            }
        }
    }
}