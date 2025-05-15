package com.example.taskman.ui.main

sealed interface MainBottomSheetType {
    data object None : MainBottomSheetType
    data class Task(val taskId: Int? = null) : MainBottomSheetType
    data class Group(val groupId: Int? = null) : MainBottomSheetType
}