package com.example.home.sheet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.control.task.TaskControl
import com.example.control.task.TaskControlViewModel

@Composable
fun TaskControlBottomSheetContent(
    taskId: Int?,
    viewModel: TaskControlViewModel = hiltViewModel(),
    onDismiss: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    TaskControl(
        uiState = state,
        onIntent = viewModel::onIntent,
        entityId = taskId,
        onBackClick = onDismiss
    )
}