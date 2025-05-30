package com.example.taskman.ui.control

import androidx.compose.ui.graphics.Color
import com.example.taskman.model.MyTask
import com.example.taskman.model.TaskType

sealed interface ControlIntent {
    data class UpdateName(val name: String) : ControlIntent
    data class UpdateIcon(val icon: String) : ControlIntent
    data class UpdateColor(val color: Color) : ControlIntent
    data object ClearError : ControlIntent
    data object ClearState : ControlIntent
    data class LoadEntity(val entityId: Int) : ControlIntent
    data object SaveEntity : ControlIntent
    data class DeleteEntity(val entityId: Int) : ControlIntent
}

sealed interface TaskControlIntent : ControlIntent {
    data class UpdateType(val type: TaskType) : TaskControlIntent
    data class UpdateDate(val date: Long) : TaskControlIntent
}

sealed interface GroupControlIntent : ControlIntent {
    data class AddTask(val task: MyTask) : GroupControlIntent
    data class RemoveTask(val task: MyTask) : GroupControlIntent
}
