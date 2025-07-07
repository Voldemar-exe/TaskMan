package com.example.control.task

import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import com.example.control.ControlIntent
import com.example.control.ControlState
import com.example.control.TaskControlIntent
import com.example.control.screen.ControlScreen
import com.example.ui.components.TaskManDatePicker
import com.example.ui.components.TaskTypeDropdownMenu

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