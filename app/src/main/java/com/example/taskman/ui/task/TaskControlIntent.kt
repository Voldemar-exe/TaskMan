package com.example.taskman.ui.task

import androidx.compose.ui.graphics.Color
import com.example.taskman.model.TaskTypes

sealed interface TaskControlIntent {
    data class UpdateName(val name: String) : TaskControlIntent
    data class UpdateIcon(val icon: Int) : TaskControlIntent
    data class UpdateColor(val color: Color) : TaskControlIntent
    data class UpdateType(val type: TaskTypes) : TaskControlIntent
    data class UpdateDate(val date: Long) : TaskControlIntent
    object SaveTask : TaskControlIntent
    object ClearTask : TaskControlIntent
    data class LoadTask(val taskId: Int) : TaskControlIntent
}