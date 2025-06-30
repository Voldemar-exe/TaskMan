package com.example.taskman.ui.main.sheet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.taskman.ui.control.task.TaskControl
import com.example.taskman.ui.control.task.TaskControlViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun TaskControlBottomSheetContent(
    taskId: Int?,
    onDismiss: () -> Unit
) {
    val viewModel = koinViewModel<TaskControlViewModel>()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    TaskControl(
        uiState = state,
        onIntent = viewModel::onIntent,
        entityId = taskId,
        onBackClick = onDismiss
    )
}