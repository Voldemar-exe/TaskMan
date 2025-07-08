package com.example.control

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shared.IntentResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class ControlViewModel (
    initialState: ControlState
) : ViewModel() {

    protected val controlState = MutableStateFlow(initialState)
    protected val baseState get() = controlState.value.base
    val uiState = controlState.asStateFlow()

    private fun updateBaseState(update: ControlState.BaseState.() -> ControlState.BaseState) {
        controlState.update {
            it.copy(base = it.base.update())
        }
    }

    protected fun processBaseIntent(intent: ControlIntent) {
        viewModelScope.launch {
            when (intent) {
                is ControlIntent.UpdateName ->
                    updateBaseState { copy(entityName = intent.name) }
                is ControlIntent.UpdateIcon ->
                    updateBaseState { copy(selectedIcon = intent.icon) }
                is ControlIntent.UpdateColor ->
                    updateBaseState { copy(selectedColor = intent.color) }
                is ControlIntent.ClearError ->
                    updateBaseState { copy(intentRes = IntentResult.None) }
                is ControlIntent.ClearState -> clearState()
                ControlIntent.SaveEntity -> saveEntity()
                is ControlIntent.LoadEntity -> loadEntity(intent.entityId)
                is ControlIntent.DeleteEntity -> deleteEntity(intent.entityId)
                else -> Unit
            }
        }
    }

    private fun clearState() {
        controlState.update {
            it.copy(
                base = ControlState.BaseState(),
                task = if (it.task != null) ControlState.TaskState() else null,
                group = if (it.group != null) ControlState.GroupState() else null
            )
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