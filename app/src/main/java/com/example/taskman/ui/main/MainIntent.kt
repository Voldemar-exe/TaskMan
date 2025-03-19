package com.example.taskman.ui.main

import com.example.taskman.model.MyTask

sealed interface MainIntent {
    data class MainSwitch(val task: MyTask) : MainIntent
    data class ShowBottomSheet(
        val type: MainState.BottomSheetType = MainState.BottomSheetType.None,
        val isEditMode: Boolean = false
    ) : MainIntent
    data object CloseBottomSheet : MainIntent
}