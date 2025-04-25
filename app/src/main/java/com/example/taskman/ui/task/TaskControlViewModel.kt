package com.example.taskman.ui.task

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskman.db.TaskDao
import com.example.taskman.model.MyTask
import com.example.taskman.model.TaskTypes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TaskControlViewModel(
    private val taskDao: TaskDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskControlState())
    val uiState: StateFlow<TaskControlState> = _uiState

    fun processIntent(intent: TaskControlIntent) {
        viewModelScope.launch {
            when (intent) {
                is TaskControlIntent.UpdateName -> updateName(intent.name)
                is TaskControlIntent.UpdateIcon -> updateIcon(intent.icon)
                is TaskControlIntent.UpdateColor -> updateColor(intent.color)
                is TaskControlIntent.UpdateType -> updateType(intent.type)
                is TaskControlIntent.UpdateDate -> updateDate(intent.date)
                TaskControlIntent.SaveTask -> saveTask()
                TaskControlIntent.ClearTask -> clearTask()
                is TaskControlIntent.LoadTask -> loadTask(intent.taskId)
            }
        }
    }

    private fun updateName(name: String) {
        _uiState.update { it.copy(taskName = name) }
    }

    private fun updateIcon(icon: Int) {
        _uiState.update { it.copy(selectedIcon = icon) }
    }

    private fun updateColor(color: Color) {
        _uiState.update { it.copy(selectedColor = color) }
    }

    private fun updateType(type: TaskTypes) {
        _uiState.update { it.copy(selectedType = type) }
    }

    private fun updateDate(date: Long) {
        _uiState.update { it.copy(selectedDate = date) }
    }

    private fun saveTask() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val task = MyTask(
                    taskId = uiState.value.taskId,
                    name = uiState.value.taskName,
                    icon = uiState.value.selectedIcon,
                    color = uiState.value.selectedColor.toArgb().toLong(),
                    type = uiState.value.selectedType.name,
                    note = uiState.value.selectedType.note, // TODO: ADD NOTES UI
                    isComplete = uiState.value.isComplete,
                    date = uiState.value.selectedDate
                )
                if (uiState.value.isEditMode) {
                    taskDao.updateTask(task)
                } else {
                    taskDao.insertTask(task)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    private fun clearTask() {
        _uiState.update { TaskControlState() }
    }

    private fun loadTask(taskId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val task = taskDao.getTaskById(taskId)
                _uiState.update { state ->
                    state.copy(
                        taskId = task.taskId,
                        taskName = task.name,
                        selectedIcon = task.icon,
                        selectedColor = Color(task.color),
                        selectedType = TaskTypes.valueOf(task.type),
                        isComplete = task.isComplete,
                        selectedDate = task.date,
                        isEditMode = true,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }
}