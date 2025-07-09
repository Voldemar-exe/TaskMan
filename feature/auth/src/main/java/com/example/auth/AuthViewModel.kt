package com.example.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.GroupRepository
import com.example.data.repository.SessionRepository
import com.example.data.repository.TaskRepository
import com.example.network.LoginRequest
import com.example.network.RegisterRequest
import com.example.network.retrofit.auth.AuthService
import com.example.shared.UserGroupWithTasks
import com.example.shared.UserTask
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authService: AuthService,
    private val taskRepository: TaskRepository,
    private val groupRepository: GroupRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() {

    private val _authState = MutableStateFlow(AuthState())
    val authState = _authState.asStateFlow()

    companion object {
        private const val TAG = "AuthViewModel"
        private const val EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    }

    fun onIntent(intent: AuthIntent) {
        Log.i(TAG, "$intent")
        when (intent) {
            is AuthIntent.Submit -> submitForm()
            is AuthIntent.ToggleMode -> toggleMode()
        }
    }

    private fun toggleMode() {
        _authState.update {
            it.copy(
                authMode = when (it.authMode) {
                    AuthMode.Login -> AuthMode.Register
                    AuthMode.Register -> AuthMode.Login
                }
            )
        }
    }

    private fun validateInput(): String? {
        val state = _authState.value
        return when {
            state.loginState.text.length < 3 -> "Логин должен содержать минимум 3 символа"
            state.authMode.isRegister && !state.emailState.text.matches(EMAIL_PATTERN.toRegex()) ->
                "Некорректный email адрес"

            state.authMode.isRegister && state.usernameState.text.length < 2 ->
                "Имя пользователя должно содержать минимум 2 символа"

            state.passwordState.text.length < 6 -> "Пароль должен содержать минимум 6 символов"
            state.authMode.isRegister && state.passwordState != state.confirmPasswordState ->
                "Пароли не совпадают"

            else -> null
        }
    }

    private fun submitForm() {
        validateInput()?.let { isVal ->
            _authState.update { it.copy(error = isVal) }
            return
        }

        viewModelScope.launch {
            _authState.update { it.copy(isLoading = true, error = null) }
            val state = _authState.value
            withContext(Dispatchers.IO) {
                val isSuccess = when (state.authMode.isRegister) {
                    false -> handleLogin(state)
                    true -> handleRegistration(state)
                }
                if (isSuccess) {
                    _authState.update {
                        it.copy(
                            isLoading = false,
                            error = null,
                            success = true
                        )
                    }
                } else {
                    _authState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    private suspend fun handleRegistration(state: AuthState): Boolean {
        val tasks = taskRepository.getAllTasksWithoutGroups()
        val groups = groupRepository.allGroupsWithTasks.first()

        val response = authService.registerUser(
            RegisterRequest(
                login = state.loginState.text.toString(),
                password = state.passwordState.text.toString(),
                email = state.emailState.text.toString(),
                username = state.usernameState.text.toString(),
                tasksWithoutGroup = tasks,
                groupsWithTasks = groups
            )
        )

        if (response == null) {
            _authState.update { it.copy(success = null, error = "Ошибка регистрации") }
            return false
        }

        syncLocalData(response.tasks, response.groups)
        return true
    }

    private suspend fun handleLogin(state: AuthState): Boolean {
        val response = authService.loginUser(
            LoginRequest(state.loginState.text.toString(), state.passwordState.text.toString())
        )

        if (response == null) {
            _authState.update { it.copy(success = null, error = "Ошибка входа") }
            return false
        }

        Log.i(TAG, "$response")

        syncLocalData(response.tasks, response.groups)
        return true
    }

    private suspend fun syncLocalData(
        tasksWithoutGroup: List<UserTask>,
        groupsWithTasks: List<UserGroupWithTasks>
    ) {
        sessionRepository.clearDatabaseData()

        tasksWithoutGroup.forEach {
            taskRepository.insertTask(it)
        }

        groupsWithTasks.forEach { group ->
            val groupId = groupRepository.insertGroup(group.group)
            group.tasks.forEach { task ->
                val taskId = taskRepository.insertTask(task)
                groupRepository.insertGroupTaskCrossRef(groupId.toInt(), taskId.toInt())
            }
        }
    }
}
