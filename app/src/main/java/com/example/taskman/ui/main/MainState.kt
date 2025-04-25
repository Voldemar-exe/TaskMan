package com.example.taskman.ui.main

import com.example.taskman.model.MyTask


data class MainState(
    val tasks: List<MyTask> = emptyList(),
    val selectedTaskId: Int? = null,
    val selectedGroupId: Int = 0,
    val selectedGroupName: String = "Все",
    val isGroupEditMode: Boolean = false,
    val bottomSheet: BottomSheetType = BottomSheetType.None,
    val error: String? = null
) {
    sealed interface BottomSheetType {
        data object None : BottomSheetType
        data class Task(val taskId: Int? = null) : BottomSheetType
        data class Group(val groupId: Int? = null) : BottomSheetType
    }
}