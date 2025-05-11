package com.example.taskman.ui.control

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.example.taskman.R
import com.example.taskman.model.MyTask
import com.example.taskman.model.TaskTypes
import com.example.taskman.ui.components.IntentResult

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
        val selectedIcon: Int = R.drawable.ic_work,
        val selectedColor: Color = Color.Red,
        val isLoading: Boolean = false,
        val intentRes: IntentResult = IntentResult.None,
        val isEditMode: Boolean = false
    )

    data class TaskState(
        val selectedType: TaskTypes = TaskTypes.Work,
        val selectedDate: Long = System.currentTimeMillis(),
        val isComplete: Boolean = false
    )

    data class GroupState(
        val allTasks: List<MyTask> = emptyList(),
        val tasksInGroup: List<MyTask> = emptyList()
    )
}