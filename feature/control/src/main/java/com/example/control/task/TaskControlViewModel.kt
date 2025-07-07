package com.example.control.task

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.control.ControlIntent
import com.example.control.ControlState
import com.example.control.ControlViewModel
import com.example.control.TaskControlIntent
import com.example.data.repository.TaskRepository
import com.example.shared.IntentResult
import com.example.shared.SharedTask
import com.example.shared.TaskType
import com.example.shared.UserTask
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class TaskControlViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ControlViewModel(
    initialState = ControlState(
        base = ControlState.BaseState(),
        task = ControlState.TaskState()
    )
) {
    companion object {
        private const val TAG = "TaskControlViewModel"
    }

    private val taskState: ControlState.TaskState
        get() = controlState.value.task ?: error("Task state is null in $TAG")

    fun onIntent(intent: ControlIntent) {
        Log.i(TAG, "intent: $intent")
        when (intent) {
            is TaskControlIntent.UpdateDate -> updateDate(intent.date)
            is TaskControlIntent.UpdateType -> updateType(intent.type)
            else -> processBaseIntent(intent)
        }
    }

    private fun updateType(type: TaskType) {
        controlState.update {
            it.copy(task = taskState.copy(selectedType = type))
        }
    }

    private fun updateDate(date: Long) {
        controlState.update {
            it.copy(task = taskState.copy(selectedDate = date))
        }
    }

    override fun saveEntity() {
        if (!validateData()) return
        viewModelScope.launch {
            // startLoading() TODO FIX TO REAL LOADING
            try {
                val task = stateToSharedTask()
                Log.w(TAG, "$baseState")
                Log.w(TAG, "$task")
                when (controlState.value.base.isEditMode) {
                    true -> taskRepository.updateWithSyncFlag(task)
                    false -> taskRepository.insertWithSyncFlag(task)
                }
                setResult(IntentResult.Success(ControlIntent.SaveEntity.toString()))
            } catch (e: Exception) {
                errorException(e)
            }
        }
    }

    override fun loadEntity(entityId: Int) {
        viewModelScope.launch {
            try {
                taskRepository.getTaskById(entityId)?.let { updateStateFromTask(it) }
            } catch (e: Exception) {
                errorException(e)
            }
        }
    }

    override fun deleteEntity(entityId: Int) {
        viewModelScope.launch {
            taskRepository.deleteTaskById(entityId)
        }
    }

    private fun stateToSharedTask() = SharedTask(
        localId = baseState.entityId ?: 0,
        serverId = baseState.serverEntityId,
        name = baseState.entityName,
        icon = baseState.selectedIcon,
        color = baseState.selectedColor,
        type = taskState.selectedType.name,
        isComplete = taskState.isComplete,
        date = taskState.selectedDate
    )

    private fun updateStateFromTask(task: UserTask) {
        controlState.update {
            it.copy(
                base = baseState.copy(
                    entityId = task.localId,
                    serverEntityId = task.serverId,
                    entityName = task.name,
                    selectedIcon = task.icon,
                    selectedColor = task.color,
                    isLoading = false,
                    intentRes = IntentResult.Success(
                        ControlIntent.LoadEntity(task.localId).toString()
                    ),
                    isEditMode = true,
                ),
                task = taskState.copy(
                    selectedType = TaskType.valueOf(task.type),
                    isComplete = task.isComplete,
                    selectedDate = task.date
                )
            )
        }
    }
}