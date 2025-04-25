package com.example.taskman.ui.task

import androidx.compose.ui.graphics.Color
import com.example.taskman.R
import com.example.taskman.model.TaskTypes

data class TaskControlState(
    val taskId: Int = 0,
    val taskName: String = "",
    val selectedIcon: Int = R.drawable.ic_work,
    val selectedColor: Color = Color.Red,
    val selectedType: TaskTypes = TaskTypes.Work,
    val selectedDate: Long = System.currentTimeMillis(),
    val isComplete: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isEditMode: Boolean = false
)