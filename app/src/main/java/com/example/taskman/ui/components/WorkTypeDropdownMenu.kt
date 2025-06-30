package com.example.taskman.ui.components

import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.taskman.model.TaskType

@Composable
fun WorkTypeDropdownMenu(
    expanded: Boolean,
    selectedTaskTypes: List<TaskType>,
    onSelectTask: (TaskType) -> Unit,
    onExpandedChange: (Boolean) -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { onExpandedChange(false) }
    ) {
        TaskType.entries.forEach { type ->
            DropdownMenuItem(
                text = { Text(text = type.ru) },
                enabled = false,
                onClick = { onSelectTask(type) },
                colors = MenuDefaults.itemColors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface
                ),
                trailingIcon = {
                    Checkbox(
                        checked = type in selectedTaskTypes,
                        onCheckedChange = { onSelectTask(type) }
                    )
                }
            )
        }
    }
}