package com.example.home

import com.example.home.sheet.HomeDestination
import com.example.shared.TaskType
import com.example.shared.UserTask
import com.example.shared.UserTaskGroup

data class HomeState(
    val visibleGroups: List<UserTaskGroup> = emptyList(),
    val visibleTasks: List<UserTask> = emptyList(),
    val selectedTaskId: Int? = null,
    val selectedGroupId: Int = -1,
    val selectedGroupName: String = "Все",
    val selectedTabIndex: Int = 1,
    val selectedTaskTypes: Set<TaskType> = emptySet(),
    val homeDestination: HomeDestination = HomeDestination.Home,
    val error: String? = null,
    val success: String? = null
)