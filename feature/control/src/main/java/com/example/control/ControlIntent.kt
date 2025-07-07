package com.example.control

import com.example.shared.ItemColor
import com.example.shared.ItemIcon
import com.example.shared.TaskType
import com.example.shared.UserTask

sealed interface ControlIntent {
    data class UpdateName(val name: String) : ControlIntent
    data class UpdateIcon(val icon: ItemIcon) : ControlIntent
    data class UpdateColor(val color: ItemColor) : ControlIntent
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
    data class AddTask(val task: UserTask) : GroupControlIntent
    data class RemoveTask(val task: UserTask) : GroupControlIntent
}
