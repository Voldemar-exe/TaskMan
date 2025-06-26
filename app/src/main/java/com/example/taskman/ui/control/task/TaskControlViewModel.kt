package com.example.taskman.ui.control.task

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.viewModelScope
import com.example.taskman.db.TaskDao
import com.example.taskman.model.MyTask
import com.example.taskman.model.TaskType
import com.example.taskman.ui.components.IntentResult
import com.example.taskman.ui.control.ControlIntent
import com.example.taskman.ui.control.ControlState
import com.example.taskman.ui.control.ControlViewModel
import com.example.taskman.ui.control.TaskControlIntent
import kotlinx.coroutines.Dispatchers
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
            startLoading()
            try {
                val task = MyTask(
                    taskId = baseState.entityId ?: 0,
                    serverId = baseState.serverEntityId,
                    name = baseState.entityName,
                    icon = baseState.selectedIcon,
                    color = baseState.selectedColor.toArgb().toLong(),
                    type = taskState.selectedType.name,
                    isComplete = taskState.isComplete,
                    date = taskState.selectedDate
                )

                when (controlState.value.base.isEditMode) {
                    true -> {
                        taskDao.updateWithSyncFlag(task)
                    }
                    false -> {
                        taskDao.insertWithSyncFlag(task)
                    }
                }
                setResult(IntentResult.Success(ControlIntent.SaveEntity.toString()))
            } catch (e: Exception) {
                errorException(e)
            }
        }
    }

    override fun loadEntity(entityId: Int) {
        viewModelScope.launch {
            startLoading()
            try {
                taskDao.getTaskById(entityId)?.let { task ->
                    controlState.update {
                        it.copy(
                            base = baseState.copy(
                                entityId = task.taskId,
                                serverEntityId = task.serverId,
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
                                selectedType = TaskType.valueOf(task.type),
                                isComplete = task.isComplete,
                                selectedDate = task.date
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                errorException(e)
            }
        }
    }

    override fun deleteEntity(entityId: Int) {
        viewModelScope.launch {
            taskDao.deleteTaskById(entityId)
        }
    }

    companion object {
        private const val TAG = "TaskControlViewModel"
    }
}