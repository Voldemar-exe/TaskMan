package com.example.taskman.ui.control.task

import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import com.example.taskman.ui.components.TaskManDatePicker
import com.example.taskman.ui.components.TaskTypeDropdownMenu
import com.example.taskman.ui.control.ControlIntent
import com.example.taskman.ui.control.ControlScreen
import com.example.taskman.ui.control.ControlState
import com.example.taskman.ui.control.TaskControlIntent


@Composable
fun TaskControl(
    uiState: ControlState,
    entityId: Int?,
    onIntent: (ControlIntent) -> Unit,
    onBackClick: () -> Unit
) {
    ControlScreen(
        uiState = uiState,
        onIntent = onIntent,
        onBackClick = onBackClick,
        entityId = entityId
    ) {
        val taskState = uiState.task
        taskState?.let {
            TaskTypeDropdownMenu(
                selectedType = taskState.selectedType,
                onTypeSelected = { onIntent(TaskControlIntent.UpdateType(it)) }
            )

            HorizontalDivider()

            TaskManDatePicker(
                selectedDate = taskState.selectedDate,
                onDateSelected = {
                    onIntent(TaskControlIntent.UpdateDate(it))
                }
            )
        }
    }
}