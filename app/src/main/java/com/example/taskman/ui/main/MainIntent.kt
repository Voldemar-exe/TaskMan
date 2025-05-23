package com.example.taskman.ui.main

import com.example.taskman.model.MyTask
import com.example.taskman.model.TaskGroup
import com.example.taskman.model.TaskType

sealed interface MainIntent {
    data class ToggleTaskCompletion(val task: MyTask) : MainIntent
    data class ShowBottomSheet(
        val type: MainBottomSheetType = MainBottomSheetType.None
    ) : MainIntent
    data class SelectTab(val tabIndex: Int) : MainIntent
    data class SelectTask(val taskId: Int) : MainIntent
    data class SelectGroup(val group: TaskGroup) : MainIntent
    data class SelectTaskType(val taskType: TaskType) : MainIntent
    data object CloseBottomSheet : MainIntent
    data object LoadTasks : MainIntent
    data object SyncData : MainIntent
}