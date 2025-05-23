package com.example.taskman.ui.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskman.api.profile.ProfileService
import com.example.taskman.ui.utils.SessionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel(
    private val profileService: ProfileService,
    private val sessionRepository: SessionRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileState())
    val uiState = _uiState.asStateFlow()

    companion object {
        private const val TAG = "ProfileViewModel"
    }

    init {
        loadProfile()
    }

    fun onIntent(intent: ProfileIntent) {
        Log.i(TAG, "$intent")
        when (intent) {
            ProfileIntent.LoadProfile -> loadProfile()
            is ProfileIntent.InfoClick ->
                _uiState.update { it.copy(isInfo = !it.isInfo) }
            is ProfileIntent.ClearProfile -> clearProfile()
            ProfileIntent.DeleteProfile -> deleteProfile()
            ProfileIntent.DeleteProfileData -> deleteProfileData()
            ProfileIntent.ClearError -> _uiState.update { it.copy(error = null) }
            ProfileIntent.ClearSuccess -> _uiState.update { it.copy(success = false) }
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                val profile = withContext(Dispatchers.IO) {
                    sessionRepository.getProfileData()
                }
                profile?.let {
                    _uiState.update {
                        it.copy(
                            username = profile.username,
                            email = profile.email,
                            isLoading = false,
                            error = null
                        )
                    }
                    return@launch
                }
                error("Profile is null")
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Ошибка загрузки профиля"
                    )
                }
            }
        }
    }

    private fun clearProfile() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                withContext(Dispatchers.IO) {
                    sessionRepository.clearSession()
                }

                _uiState.update {
                    it.copy(
                        username = "",
                        email = "",
                        isLoading = false,
                        error = null,
                        success = true
                    )
                }
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Ошибка очистки профиля"
                    )
                }
            }
        }
    }

    private fun deleteProfileData() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, error = null) }
        if (profileService.deleteUserData()) {
            sessionRepository.clearDatabaseData()
            _uiState.update { it.copy(success = true) }
        } else {
            _uiState.update { it.copy(error = "Не удалось удалить данные пользователя") }
        }
        return@launch
    }

    private fun deleteProfile() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, error = null) }
        if (profileService.deleteUser()) {
            clearProfile()
            _uiState.update { it.copy(success = true) }
        } else {
            _uiState.update { it.copy(error = "Не удалось удалить данные пользователя") }
        }
        return@launch
    }
}
