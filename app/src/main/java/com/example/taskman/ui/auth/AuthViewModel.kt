package com.example.taskman.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.dto.GroupDto
import com.example.shared.dto.TaskDto
import com.example.shared.request.LoginRequest
import com.example.shared.request.RegisterRequest
import com.example.taskman.api.auth.AuthService
import com.example.taskman.db.GroupDao
import com.example.taskman.db.GroupTaskCrossRef
import com.example.taskman.db.TaskDao
import com.example.taskman.model.MyTask
import com.example.taskman.model.TaskGroup
import com.example.taskman.ui.utils.SessionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthViewModel(
    private val authService: AuthService,
    private val taskDao: TaskDao,
    private val groupDao: GroupDao,
    private val sessionRepository: SessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthState())
    val uiState: StateFlow<AuthState> = _uiState.asStateFlow()

    companion object {
        private const val TAG = "AuthViewModel"
        private const val EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    }

    fun onIntent(intent: AuthIntent) {
        Log.i(TAG, "$intent")
        when (intent) {
            is AuthIntent.UpdateLogin ->
                _uiState.update { it.copy(login = intent.login) }

            is AuthIntent.UpdateEmail ->
                _uiState.update { it.copy(email = intent.email) }

            is AuthIntent.UpdateUsername ->
                _uiState.update { it.copy(username = intent.username) }

            is AuthIntent.UpdatePassword ->
                _uiState.update { it.copy(password = intent.password) }

            is AuthIntent.UpdateConfirmPassword ->
                _uiState.update { it.copy(confirmPassword = intent.confirmPassword) }

            is AuthIntent.Submit -> submitForm()
            is AuthIntent.ToggleMode -> toggleMode()
        }
    }

    private fun toggleMode() {
        _uiState.update {
            it.copy(
                authMode = when (it.authMode) {
                    AuthMode.Login -> AuthMode.Register
                    AuthMode.Register -> AuthMode.Login
                }
            )
        }
    }

    private fun validateInput(): String? {
        val state = _uiState.value
        return when {
            state.login.length < 3 -> "Логин должен содержать минимум 3 символа"
            state.authMode.isRegister && !state.email.matches(EMAIL_PATTERN.toRegex()) ->
                "Некорректный email адрес"

            state.authMode.isRegister && state.username.length < 2 ->
                "Имя пользователя должно содержать минимум 2 символа"

            state.password.length < 6 -> "Пароль должен содержать минимум 6 символов"
            state.authMode.isRegister && state.password != state.confirmPassword ->
                "Пароли не совпадают"

            else -> null
        }
    }

    private fun submitForm() {
        validateInput()?.let { isVal ->
            _uiState.update { it.copy(error = isVal) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val state = _uiState.value
            withContext(Dispatchers.IO) {
                val isSuccess = when (state.authMode.isRegister) {
                    false -> handleLogin(state)
                    true -> handleRegistration(state)
                }
                if (isSuccess) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = null,
                            success = true
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    private suspend fun handleRegistration(state: AuthState): Boolean {
        val tasks = taskDao.getAllTasksWithoutGroups().map { it.toDto() }
        val groups = groupDao.getAllGroupsWithTasksFlow().first().map { group ->
            GroupDto(
                group.group.serverId ?: 0,
                group.group.name,
                group.group.icon,
                group.group.color,
                group.tasks.map { it.toDto() }
            )
        }

        val response = authService.registerUser(
            RegisterRequest(
                login = state.login,
                password = state.password,
                email = state.email,
                username = state.username,
                tasksWithoutGroup = tasks,
                groupsWithTasks = groups
            )
        )

        if (response == null) {
            _uiState.update { it.copy(success = null, error = "Ошибка регистрации") }
            return false
        }

        syncLocalData(response.tasks, response.groups)
        return true
    }

    private suspend fun handleLogin(state: AuthState): Boolean {
        val response = authService.loginUser(
            LoginRequest(state.login, state.password)
        )

        if (response == null) {
            _uiState.update { it.copy(success = null, error = "Ошибка входа") }
            return false
        }

        Log.i(TAG, "$response")

        syncLocalData(response.tasks, response.groups)
        return true
    }

    private suspend fun syncLocalData(
        tasksWithoutGroup: List<TaskDto>,
        groupsWithTasks: List<GroupDto>
    ) {
        sessionRepository.clearDatabaseData()

        tasksWithoutGroup.forEach {
            taskDao.insertTask(MyTask(it))
        }

        groupsWithTasks.forEach { group ->
            val groupId = groupDao.insertGroup(TaskGroup(group))
            group.tasks.forEach { task ->
                val taskId = taskDao.insertTask(MyTask(task))
                groupDao.insertGroupTaskCrossRef(
                    GroupTaskCrossRef(
                        groupId = groupId.toInt(),
                        taskId = taskId.toInt()
                    )
                )
            }
        }
    }
}
