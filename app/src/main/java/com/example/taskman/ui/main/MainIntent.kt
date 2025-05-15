package com.example.taskman.ui.main

import com.example.taskman.model.MyTask
import com.example.taskman.model.TaskGroup

sealed interface MainIntent {
    data class MainSwitch(val task: MyTask) : MainIntent
    data class ShowBottomSheet(
        val type: MainBottomSheetType = MainBottomSheetType.None
    ) : MainIntent

    data class ChangeTab(val tabIndex: Int) : MainIntent
    data class SelectTask(val taskId: Int) : MainIntent
    data class SelectGroup(val group: TaskGroup) : MainIntent
    data object CloseBottomSheet : MainIntent
    data object LoadTasks : MainIntent
    data class ChangeEditMode(val isEdit: Boolean) : MainIntent
}