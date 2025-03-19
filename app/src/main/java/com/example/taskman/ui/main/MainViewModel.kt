package com.example.taskman.ui.main

import androidx.lifecycle.ViewModel
import com.example.taskman.model.MyTask
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MainState())
    val uiState = _uiState.asStateFlow()

    fun processIntent(intent: MainIntent) {
        when (intent) {
            is MainIntent.MainSwitch -> toggleTaskCompletion(intent.task)
            is MainIntent.ShowBottomSheet -> showBottomSheet(intent.type, intent.isEditMode)
            MainIntent.CloseBottomSheet -> closeBottomSheet()
        }
    }

    private fun toggleTaskCompletion(task: MyTask) {
        _uiState.update { state ->
            state.copy(tasks = state.tasks.map {
                if (it.id == task.id) it.copy(isComplete = !it.isComplete)
                else it
            })
        }
    }

    private fun showBottomSheet(
        type: MainState.BottomSheetType,
        isEditMode: Boolean
    ) {
        _uiState.update {
            it.copy(
                bottomSheet = type,
                isEditMode = isEditMode
            )
        }
    }

    private fun closeBottomSheet() {
        _uiState.update {
            it.copy(
                bottomSheet = MainState.BottomSheetType.None,
                isEditMode = false
            )
        }
    }
}