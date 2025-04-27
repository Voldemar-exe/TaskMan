package com.example.taskman.ui.task

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.viewModelScope
import com.example.taskman.db.TaskDao
import com.example.taskman.model.MyTask
import com.example.taskman.model.TaskTypes
import com.example.taskman.ui.components.ControlIntent
import com.example.taskman.ui.components.ControlState
import com.example.taskman.ui.components.ControlViewModel
import com.example.taskman.ui.components.IntentResult
import com.example.taskman.ui.components.TaskControlIntent
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TaskControlViewModel(
    private val taskDao: TaskDao
) : ControlViewModel(
    initialState = ControlState(
        base = ControlState.BaseState(),
        task = ControlState.TaskState()
    )
) {

    fun processIntent(intent: ControlIntent) {
        Log.i(TAG, "intent: $intent")
        when (intent) {
            is ControlIntent.LoadEntity -> loadEntity(intent.entityId)
            ControlIntent.SaveEntity -> saveEntity()
            is TaskControlIntent.UpdateDate -> updateDate(intent.date)
            is TaskControlIntent.UpdateType -> updateType(intent.type)
            else -> processBaseIntent(intent)
        }
    }

    private fun updateType(type: TaskTypes) {
        _uiState.update {
            it.copy(task = it.task?.copy(selectedType = type))
        }
    }

    private fun updateDate(date: Long) {
        _uiState.update {
            it.copy(task = it.task?.copy(selectedDate = date))
        }
    }

    override fun saveEntity() {
        if (!validateData()) return

        viewModelScope.launch {
            startLoading()

            val baseState = _uiState.value.base
            _uiState.value.task?.let { taskState ->
                try {
                    val task = MyTask(
                        taskId = baseState.entityId ?: 0,
                        name = baseState.entityName,
                        icon = baseState.selectedIcon,
                        color = baseState.selectedColor.toArgb().toLong(),
                        type = taskState.selectedType.name,
                        note = taskState.selectedType.note, // TODO: ADD NOTES UI
                        isComplete = taskState.isComplete,
                        date = taskState.selectedDate
                    )

                    viewModelScope.launch {
                        if (baseState.isEditMode) {
                            taskDao.updateTask(task)
                        } else {
                            taskDao.insertTask(task)
                        }
                        setResult(IntentResult.Success(ControlIntent.SaveEntity.toString()))
                    }
                } catch (e: Exception) {
                    errorException(e)
                }
            }
        }
    }

    override fun loadEntity(entityId: Int) {
        viewModelScope.launch {
            startLoading()

            val baseState = _uiState.value.base
            _uiState.value.task?.let { taskState ->
                try {
                    taskDao.getTaskById(entityId)?.let { task ->
                        _uiState.update {
                            it.copy(
                                base = baseState.copy(
                                    entityId = task.taskId,
                                    entityName = task.name,
                                    selectedIcon = task.icon,
                                    selectedColor = Color(task.color),
                                    isLoading = false,
                                    intentRes = IntentResult.Success(
                                        ControlIntent.LoadEntity(entityId).toString()
                                    ),
                                    isEditMode = true,
                                ),
                                task = taskState.copy(
                                    selectedType = TaskTypes.valueOf(task.type),
                                    isComplete = task.isComplete,
                                    selectedDate = task.date
                                )
                            )
                        }
                        _uiState.update { state ->
                            state
                        }
                    }
                } catch (e: Exception) {
                    errorException(e)
                }
            }
        }
    }

}