package com.example.taskman.ui.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


abstract class ControlViewModel(
    initialState: ControlState
) : ViewModel() {

    protected val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<ControlState> = _uiState

    protected fun updateBaseState(update: ControlState.BaseState.() -> ControlState.BaseState) {
        _uiState.update {
            it.copy(base = it.base.update())
        }
    }

    companion object {
        const val TAG = "Control"
    }

    protected fun processBaseIntent(intent: ControlIntent) = viewModelScope.launch {
        when (intent) {
            is ControlIntent.UpdateName -> updateBaseState { copy(entityName = intent.name) }
            is ControlIntent.UpdateIcon -> updateBaseState { copy(selectedIcon = intent.icon) }
            is ControlIntent.UpdateColor -> updateBaseState { copy(selectedColor = intent.color) }
            is ControlIntent.ClearError -> updateBaseState { copy(intentRes = IntentResult.None) }
            is ControlIntent.ClearState -> _uiState.update {
                it.copy(
                    base = ControlState.BaseState(),
                    task = if (it.task != null) ControlState.TaskState() else null,
                    group = if (it.group != null) ControlState.GroupState() else null
                    // TODO reduce this code
                )
            }

            else -> null
        }
    }

    protected fun startLoading() {
        _uiState.update { it.copy(base = it.base.copy(isLoading = true)) }
    }

    protected fun validateData(): Boolean {
        return when {
            _uiState.value.base.entityName.isBlank() -> {
                setResult(
                    IntentResult.Error(
                        "Название не может быть пустым"
                    )
                )
                false
            }

            _uiState.value.group != null -> {
                if (_uiState.value.group!!.tasksInGroup.isEmpty()) {
                    setResult(
                        IntentResult.Error(
                            "Выберите хотя бы 1 задачу"
                        )
                    )
                    false
                } else {
                    true
                }
            }

            else -> true
        }
    }

    protected fun setResult(result: IntentResult) {
        _uiState.update {
            it.copy(
                base = it.base.copy(
                    intentRes = result,
                    isLoading = false
                )
            )
        }
    }

    protected fun errorException(e: Exception) {
        _uiState.update {
            it.copy(
                base = it.base.copy(
                    intentRes = IntentResult.Error(
                        e.message
                    ),
                    isLoading = false
                ),
            )
        }
    }

    protected abstract fun saveEntity()

    protected abstract fun loadEntity(entityId: Int)
}