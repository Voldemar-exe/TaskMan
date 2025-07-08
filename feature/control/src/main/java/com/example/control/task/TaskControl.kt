package com.example.control.task

import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.control.TaskControlIntent
import com.example.control.screen.ControlScreen
import com.example.ui.components.TaskManDatePicker
import com.example.ui.components.TaskTypeDropdownMenu

@Composable
fun TaskControl(
    viewModel: TaskControlViewModel = hiltViewModel(),
    taskId: Int?,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ControlScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        onBackClick = onBackClick,
        entityId = taskId
    ) {
        val taskState = uiState.task
        taskState?.let {
            TaskTypeDropdownMenu(
                selectedType = taskState.selectedType,
                onTypeSelected = { viewModel.onIntent(TaskControlIntent.UpdateType(it)) }
            )

            HorizontalDivider()

            TaskManDatePicker(
                selectedDate = taskState.selectedDate,
                onDateSelected = {
                    viewModel.onIntent(TaskControlIntent.UpdateDate(it))
                }
            )
        }
    }
}