package com.example.taskman.ui.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskman.model.MyOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ThemeViewModel(
    private val themeRepository: ThemeRepository
) : ViewModel() {
    private val _isDarkTheme = MutableStateFlow(themeRepository.isDarkTheme())
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    init {
        viewModelScope.launch {
            themeRepository.getThemeFlow().collect { newTheme ->
                _isDarkTheme.value = newTheme
            }
        }
    }

    fun processOption(option: MyOption) {
        if (option.name == "Темная тема") {
            toggleTheme()
        }
    }

    private fun toggleTheme() {
        val newTheme = !_isDarkTheme.value
        themeRepository.saveTheme(newTheme)
        _isDarkTheme.value = newTheme
    }
}