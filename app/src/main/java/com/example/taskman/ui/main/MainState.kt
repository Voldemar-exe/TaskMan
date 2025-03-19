package com.example.taskman.ui.main

import com.example.taskman.model.MyTask


data class MainState(
    val tasks: List<MyTask> = listOf(
        MyTask(id = 1, name = "Test1"),
        MyTask(id = 2, name = "Test2"),
        MyTask(id = 3, name = "Test3")
    ),
    val bottomSheet: BottomSheetType = BottomSheetType.None,
    val isEditMode: Boolean = false
) {
    sealed interface BottomSheetType {
        data object None : BottomSheetType
        data object Task : BottomSheetType
        data object Group : BottomSheetType
    }
}