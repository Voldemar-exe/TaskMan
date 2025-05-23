package com.example.taskman.ui.main

import com.example.taskman.model.MyTask
import com.example.taskman.model.TaskGroup
import com.example.taskman.model.TaskType

data class MainState(
    val visibleGroups: List<TaskGroup> = emptyList(),
    val visibleTasks: List<MyTask> = emptyList(),
    val selectedTaskId: Int? = null,
    val selectedGroupId: Int = -1,
    val selectedGroupName: String = "Все",
    val selectedTabIndex: Int = 1,
    val selectedTaskTypes: Set<TaskType> = emptySet(),
    val bottomSheet: MainBottomSheetType = MainBottomSheetType.None,
    val error: String? = null,
    val success: String? = null
)