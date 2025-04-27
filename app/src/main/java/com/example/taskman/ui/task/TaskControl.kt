package com.example.taskman.ui.task

import androidx.compose.runtime.Composable
import com.example.taskman.ui.components.ControlIntent
import com.example.taskman.ui.components.ControlScreen
import com.example.taskman.ui.components.ControlState
import com.example.taskman.ui.components.TaskControlIntent
import com.example.taskman.ui.components.TaskDatePicker
import com.example.taskman.ui.components.TaskTypeDropdownMenu


@Composable
fun TaskControl(
    uiState: ControlState,
    entityId: Int?,
    processIntent: (ControlIntent) -> Unit,
    onBackClick: () -> Unit
) {
    ControlScreen(
        uiState = uiState,
        processIntent = processIntent,
        onBackClick = onBackClick,
        entityId = entityId
    ) {
        val taskState = uiState.task
        taskState?.let {
            TaskTypeDropdownMenu(
                selectedType = taskState.selectedType,
                onTypeSelected = { processIntent(TaskControlIntent.UpdateType(it)) }
            )

            TaskDatePicker(
                selectedDate = taskState.selectedDate,
                onDateSelected = {
                    processIntent(TaskControlIntent.UpdateDate(it))
                }
            )
        }
    }
}