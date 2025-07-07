package com.example.control

import androidx.compose.runtime.Immutable
import com.example.shared.IntentResult
import com.example.shared.ItemColor
import com.example.shared.ItemIcon
import com.example.shared.TaskType
import com.example.shared.UserIcon
import com.example.shared.UserTask

@Immutable
data class ControlState(
    val base: BaseState = BaseState(),
    val task: TaskState? = null,
    val group: GroupState? = null
) {
    data class BaseState(
        val entityId: Int? = null,
        val serverEntityId: Int? = null,
        val entityName: String = "",
        val selectedIcon: ItemIcon = UserIcon.Work,
        val selectedColor: ItemColor = ItemColor.Red,
        val isLoading: Boolean = false,
        val intentRes: IntentResult = IntentResult.None,
        val isEditMode: Boolean = false
    )

    data class TaskState(
        val selectedType: TaskType = TaskType.Work,
        val selectedDate: Long = System.currentTimeMillis(),
        val isComplete: Boolean = false
    )

    data class GroupState(
        val allTasks: List<UserTask> = emptyList(),
        val tasksInGroup: List<UserTask> = emptyList()
    )
}