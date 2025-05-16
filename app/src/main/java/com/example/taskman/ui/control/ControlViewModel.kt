package com.example.taskman.ui.control

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskman.ui.components.IntentResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


abstract class ControlViewModel(
    initialState: ControlState
) : ViewModel() {

    companion object {
        const val TAG = "ControlViewModel"
    }

    protected val controlState = MutableStateFlow(initialState)
    val uiState = controlState.asStateFlow()

    protected val baseState: ControlState.BaseState
        get() = controlState.value.base

    protected fun updateBaseState(update: ControlState.BaseState.() -> ControlState.BaseState) {
        controlState.update {
            it.copy(base = it.base.update())
        }
    }

    init {
        Log.i(TAG, "init with $initialState")
    }

    protected fun processBaseIntent(intent: ControlIntent) = viewModelScope.launch {
        when (intent) {
            is ControlIntent.UpdateName -> updateBaseState { copy(entityName = intent.name) }
            is ControlIntent.UpdateIcon -> updateBaseState { copy(selectedIcon = intent.icon) }
            is ControlIntent.UpdateColor -> updateBaseState { copy(selectedColor = intent.color) }
            is ControlIntent.ClearError -> updateBaseState { copy(intentRes = IntentResult.None) }
            is ControlIntent.ClearState -> controlState.update {
                it.copy(
                    base = ControlState.BaseState(),
                    task = if (it.task != null) ControlState.TaskState() else null,
                    group = if (it.group != null) ControlState.GroupState() else null
                    // TODO reduce this code
                )
            }

            ControlIntent.SaveEntity -> saveEntity()
            is ControlIntent.LoadEntity -> loadEntity(intent.entityId)
            is ControlIntent.DeleteEntity -> deleteEntity(intent.entityId)
            else -> null
        }
    }

    protected fun startLoading() {
        controlState.update { it.copy(base = it.base.copy(isLoading = true)) }
    }

    protected fun validateData(): Boolean {
        return when {
            controlState.value.base.entityName.isBlank() -> {
                setResult(
                    IntentResult.Error(
                        "Название не может быть пустым"
                    )
                )
                false
            }

            controlState.value.group != null -> {
                if (controlState.value.group!!.tasksInGroup.isEmpty()) {
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
        controlState.update {
            it.copy(
                base = it.base.copy(
                    intentRes = result,
                    isLoading = false
                )
            )
        }
    }

    protected fun errorException(e: Exception) {
        controlState.update {
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

    protected abstract fun deleteEntity(entityId: Int)

}