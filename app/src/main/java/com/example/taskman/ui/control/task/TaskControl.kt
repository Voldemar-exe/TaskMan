package com.example.taskman.ui.control.task

import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import com.example.taskman.ui.components.TaskDatePicker
import com.example.taskman.ui.components.TaskTypeDropdownMenu
import com.example.taskman.ui.control.ControlIntent
import com.example.taskman.ui.control.ControlScreen
import com.example.taskman.ui.control.ControlState
import com.example.taskman.ui.control.TaskControlIntent


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

            HorizontalDivider()

            TaskDatePicker(
                selectedDate = taskState.selectedDate,
                onDateSelected = {
                    processIntent(TaskControlIntent.UpdateDate(it))
                }
            )
        }
    }
}