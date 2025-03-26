package com.example.taskman.ui.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class OptionViewModel(
    private val themeRepository: ThemeRepository
) : ViewModel() {
    val isDarkTheme: StateFlow<Boolean> = themeRepository.getThemeFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = themeRepository.isDarkTheme()
        )

    fun toggleTheme() {
        themeRepository.saveTheme(!isDarkTheme.value)
    }
}