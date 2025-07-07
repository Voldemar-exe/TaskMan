package com.example.home

import com.example.home.sheet.MainBottomSheetType
import com.example.shared.TaskType
import com.example.shared.UserTask
import com.example.shared.UserTaskGroup

sealed interface MainIntent {
    data class ToggleTaskCompletion(val task: UserTask) : MainIntent
    data class ShowBottomSheet(
        val type: MainBottomSheetType = MainBottomSheetType.None
    ) : MainIntent
    data class SelectTab(val tabIndex: Int) : MainIntent
    data class SelectTask(val taskId: Int) : MainIntent
    data class SelectGroup(val group: UserTaskGroup) : MainIntent
    data class SelectTaskType(val taskType: TaskType) : MainIntent
    data object CloseBottomSheet : MainIntent
    data object SyncData : MainIntent
}