package com.example.taskman.ui.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskman.api.auth.ProfileData
import com.example.taskman.api.profile.ProfileService
import com.example.taskman.ui.utils.SessionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel(
    private val profileService: ProfileService,
    private val sessionRepository: SessionRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileState())
    val uiState: StateFlow<ProfileState> = _uiState.asStateFlow()

    companion object {
        private const val TAG = "ProfileViewModel"
        private const val EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    }

    init {
        loadProfile()
    }

    fun onIntent(intent: ProfileIntent) {
        Log.i(TAG, "$intent")
        when (intent) {
            is ProfileIntent.InfoClick ->
                _uiState.update { it.copy(isInfo = !it.isInfo) }

            is ProfileIntent.UpdateUserName ->
                _uiState.update { it.copy(userName = intent.userName, error = null) }

            is ProfileIntent.UpdateEmail ->
                _uiState.update { it.copy(email = intent.email, error = null) }

            is ProfileIntent.UpdateLogin ->
                _uiState.update { it.copy(login = intent.login, error = null) }

            is ProfileIntent.SaveProfile -> saveProfile()

            is ProfileIntent.ClearProfile -> clearProfile()
            ProfileIntent.DeleteProfile -> deleteProfile()
            ProfileIntent.DeleteProfileData -> deleteProfileData()
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
                            userName = profile.username.orEmpty(),
//                            email = profile.email,
                            login = profile.login,
                            isLoading = false,
                            error = null
                        )
                    }
                    return@launch
                }
                error("Profile is null")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading profile", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Ошибка загрузки профиля"
                    )
                }
            }
        }
    }

    private fun validateInput(): String? {
        val state = _uiState.value
        return when {
            state.userName.length < 2 -> "Имя пользователя должно содержать минимум 2 символа"
            !state.email.matches(EMAIL_PATTERN.toRegex()) -> "Некорректный email адрес"
            state.login.length < 3 -> "Логин должен содержать минимум 3 символа"
            else -> null
        }
    }
    private fun saveProfile() {
        val validationError = validateInput()
        if (validationError != null) {
            _uiState.update { it.copy(error = validationError) }
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                withContext(Dispatchers.IO) {
                    sessionRepository.updateProfileData(
                        ProfileData(
                            token = "",
                            username = _uiState.value.userName,
//                        email = _uiState.value.email,
                            login = _uiState.value.login
                        )
                    )
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = null,
                        success = true
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error saving profile", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Ошибка сохранения профиля"
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
                        userName = "",
                        email = "",
                        login = "",
                        isLoading = false,
                        error = null,
                        success = true
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error clearing profile", e)
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
        sessionRepository.getProfileData()?.let {
            if (profileService.deleteUserData(it.login)) {
                sessionRepository.clearSession()
            }
        }
    }

    private fun deleteProfile() = viewModelScope.launch {
        sessionRepository.getProfileData()?.let {
            if (profileService.deleteUser(it.login)) {
                sessionRepository.clearSession()
            }
        }
    }
}
