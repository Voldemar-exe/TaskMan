package com.example.taskman.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.request.LoginRequest
import com.example.shared.request.RegisterRequest
import com.example.shared.response.LoginResponse
import com.example.taskman.api.auth.AuthService
import com.example.taskman.db.GroupDao
import com.example.taskman.db.GroupTaskCrossRef
import com.example.taskman.db.TaskDao
import com.example.taskman.model.MyTask
import com.example.taskman.model.TaskGroup
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authService: AuthService,
    private val taskDao: TaskDao,
    private val groupDao: GroupDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthState())
    val uiState: StateFlow<AuthState> = _uiState.asStateFlow()

    companion object {
        private const val TAG = "AuthViewModel"
    }

    fun onIntent(intent: AuthIntent) {
        Log.i(TAG, "$intent")
        when (intent) {
            is AuthIntent.UpdateLogin -> updateLogin(intent.login)
            is AuthIntent.UpdatePassword -> updatePassword(intent.password)
            is AuthIntent.UpdateConfirmPassword -> updateConfirmPassword(intent.confirmPassword)
            is AuthIntent.Submit -> submitForm()
            is AuthIntent.ToggleMode -> toggleMode()
        }
    }

    private fun updateLogin(login: String) {
        _uiState.update { currentState ->
            currentState.copy(
                login = login,
                error = null
            )
        }
    }

    private fun updatePassword(password: String) {
        _uiState.update { currentState ->
            currentState.copy(
                password = password,
                error = null
            )
        }
    }

    private fun updateConfirmPassword(confirmPassword: String) {
        _uiState.update { currentState ->
            currentState.copy(
                confirmPassword = confirmPassword,
                error = null
            )
        }
    }

    private fun submitForm() {
        val state = _uiState.value

        if (state.isRegister && state.password != state.confirmPassword) {
            _uiState.update { it.copy(error = "Пароли не совпадают") }
            return
        }

        viewModelScope.launch {
            val result = runCatching {
                if (state.isRegister) {
                    register(state.login, state.password)
                } else {
                    login(state.login, state.password)
                }
            }

            result.fold(
                onSuccess = { response ->
                    if (state.isRegister) {
                        val registerResult = response as? String
                        if (registerResult.isNullOrBlank()) {
                            _uiState.update {
                                it.copy(error = "Ошибка регистрации")
                            }
                        } else {
                            _uiState.update {
                                it.copy(error = null, success = true)
                            }
                        }
                    } else {
                        val loginResponse = response as? LoginResponse
                        val tasksWithoutGroup = loginResponse?.tasks
                        val groupsWithTasks = loginResponse?.groups
                        tasksWithoutGroup?.forEach {
                            taskDao.insertTask(MyTask(it))
                        }
                        groupsWithTasks?.forEach { group ->
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

                        if (loginResponse?.token.isNullOrBlank()) {
                            _uiState.update {
                                it.copy(error = "Ошибка входа")
                            }
                        } else {
                            _uiState.update {
                                it.copy(error = null, success = true)
                            }
                        }
                    }
                },
                onFailure = { throwable ->
                    Log.e("AuthViewModel", "Network error", throwable)
                    _uiState.update {
                        it.copy(error = "Сетевая ошибка. Попробуйте ещё раз.")
                    }
                }
            )
        }
    }

    // TODO ADD USERNAME AND EMAIL
    private suspend fun register(login: String, password: String): String? =
        authService.registerUser(RegisterRequest(login, password, "", ""))

    private suspend fun login(login: String, password: String): LoginResponse? =
        authService.loginUser(LoginRequest(login, password))

    private fun toggleMode() {
        _uiState.update { currentState ->
            currentState.copy(
                isRegister = !currentState.isRegister,
                error = null
            )
        }
    }

}