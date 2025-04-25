package com.example.taskman.ui.group

import androidx.compose.ui.graphics.Color
import com.example.taskman.R
import com.example.taskman.model.MyTask

data class GroupControlState(
    val groupId: Int? = null,
    val groupName: String = "",
    val selectedIcon: Int = R.drawable.ic_work,
    val selectedColor: Color = Color.Red,
    val allTasks: List<MyTask> = emptyList(),
    val tasksInGroup: List<MyTask> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isEditMode: Boolean = false
)
