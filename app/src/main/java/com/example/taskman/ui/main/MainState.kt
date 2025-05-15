package com.example.taskman.ui.main

import com.example.taskman.model.MyTask
import com.example.taskman.model.TaskType

data class MainState(
    val tasks: List<MyTask> = emptyList(),
    val selectedTaskId: Int? = null,
    val selectedGroupId: Int = -1,
    val selectedGroupName: String = "Все",
    val selectedTabIndex: Int = 0,
    val selectedTaskTypes: List<TaskType> = emptyList<TaskType>(),
    val tabs: List<String> = listOf("Все", "Незавершённые", "Завершённые"),
    val isGroupEditMode: Boolean = false,
    val bottomSheet: MainBottomSheetType = MainBottomSheetType.None,
    val error: String? = null
)