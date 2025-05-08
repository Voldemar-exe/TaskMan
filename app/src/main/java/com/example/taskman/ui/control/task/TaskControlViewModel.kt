package com.example.taskman.ui.control.task

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.viewModelScope
import com.example.taskman.api.task.TaskRequest
import com.example.taskman.api.task.TaskService
import com.example.taskman.db.TaskDao
import com.example.taskman.model.MyTask
import com.example.taskman.model.TaskTypes
import com.example.taskman.ui.components.IntentResult
import com.example.taskman.ui.control.ControlIntent
import com.example.taskman.ui.control.ControlState
import com.example.taskman.ui.control.ControlViewModel
import com.example.taskman.ui.control.TaskControlIntent
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TaskControlViewModel(
    private val taskDao: TaskDao,
    private val taskService: TaskService
) : ControlViewModel(
    initialState = ControlState(
        base = ControlState.BaseState(),
        task = ControlState.TaskState()
    )
) {
    private val taskState: ControlState.TaskState
        get() = controlState.value.task ?: throw IllegalStateException("Task state is null in $TAG")

    fun onIntent(intent: ControlIntent) {
        Log.i(TAG, "intent: $intent")
        when (intent) {
            is TaskControlIntent.UpdateDate -> updateDate(intent.date)
            is TaskControlIntent.UpdateType -> updateType(intent.type)
            else -> processBaseIntent(intent)
        }
    }

    private fun updateType(type: TaskTypes) {
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
                val task = createTaskFromState()

                when (controlState.value.base.isEditMode) {
                    true -> {
                        taskDao.updateTask(task)
                        updateToServer(task)
                    }

                    false -> {
                        insertToServer(task.copy(taskId = taskDao.insertTask(task).toInt()))
                    }
                }
                setResult(IntentResult.Success(ControlIntent.SaveEntity.toString()))
            } catch (e: Exception) {
                errorException(e)
            }
        }
    }

    private suspend fun insertToServer(task: MyTask) {
        taskService.createTask(TaskRequest(task))?.let {
            Log.i("TEST", it.toString())
        }
    }

    private suspend fun updateToServer(task: MyTask) {
        taskService.updateTask(TaskRequest(task)).let {
            Log.i("TEST", it.toString())
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
                }
            } catch (e: Exception) {
                errorException(e)
            }
        }
    }
    // TODO MAYBE DELETE BECAUSE SYNC
    /*override fun deleteEntity(entityId: Int) {
        viewModelScope.launch {
            taskDao.deleteTaskById(entityId)?.let {
                deleteFromServer(entityId)
            }
        }
    }*/

    private suspend fun deleteFromServer(taskId: Int) {
        taskService.deleteTask(taskId)
    }

    private fun createTaskFromState(): MyTask {
        return MyTask(
            taskId = baseState.entityId ?: 0,
            name = baseState.entityName,
            icon = baseState.selectedIcon,
            color = baseState.selectedColor.toArgb().toLong(),
            type = taskState.selectedType.name,
            note = taskState.selectedType.note,
            isComplete = taskState.isComplete,
            date = taskState.selectedDate
        )
    }

    companion object {
        private const val TAG = "TaskControlViewModel"
    }
}