package com.example.home

import com.example.home.sheet.HomeDestination
import com.example.shared.TaskType
import com.example.shared.UserTask
import com.example.shared.UserTaskGroup

sealed interface HomeIntent {
    data class ToggleTaskCompletion(val task: UserTask) : HomeIntent
    data class MoveTo(val type: HomeDestination = HomeDestination.Home) : HomeIntent
    data class SelectTab(val tabIndex: Int) : HomeIntent
    data class SelectTask(val taskId: Int) : HomeIntent
    data class SelectGroup(val group: UserTaskGroup) : HomeIntent
    data class SelectTaskType(val taskType: TaskType) : HomeIntent
    data object SyncData : HomeIntent
}