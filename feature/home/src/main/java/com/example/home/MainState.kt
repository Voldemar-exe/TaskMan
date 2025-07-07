package com.example.home

import com.example.home.sheet.MainBottomSheetType
import com.example.shared.TaskType
import com.example.shared.UserTask
import com.example.shared.UserTaskGroup

data class MainState(
    val visibleGroups: List<UserTaskGroup> = emptyList(),
    val visibleTasks: List<UserTask> = emptyList(),
    val selectedTaskId: Int? = null,
    val selectedGroupId: Int = -1,
    val selectedGroupName: String = "Все",
    val selectedTabIndex: Int = 1,
    val selectedTaskTypes: Set<TaskType> = emptySet(),
    val bottomSheet: MainBottomSheetType = MainBottomSheetType.None,
    val error: String? = null,
    val success: String? = null
)