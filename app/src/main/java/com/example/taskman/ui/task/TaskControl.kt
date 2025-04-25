package com.example.taskman.ui.task

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.taskman.ui.components.GridDialog
import com.example.taskman.ui.components.TaskDatePicker
import com.example.taskman.ui.components.TaskTypeDropdownMenu
import com.example.taskman.ui.utils.TaskManAppData

@Composable
fun TaskControl(
    modifier: Modifier = Modifier,
    uiState: TaskControlState,
    processIntent: (TaskControlIntent) -> Unit,
    taskId: Int?,
    onBackClick: () -> Unit
) {

    LaunchedEffect(taskId) {
        if (taskId != null) {
            processIntent(TaskControlIntent.LoadTask(taskId))
        } else {
            processIntent(TaskControlIntent.ClearTask)
        }
    }

    Scaffold(
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        processIntent(TaskControlIntent.SaveTask)
                        onBackClick()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    Text(text = if (taskId == null) "Добавить" else "Сохранить")
                }
                TextButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    Text(text = "Назад")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.taskName,
                onValueChange = { processIntent(TaskControlIntent.UpdateName(it)) },
                label = { Text("Название") }
            )
            ListItem(
                headlineContent = {
                    Text(text = "Иконка")
                },
                trailingContent = {
                    GridDialog(
                        items = TaskManAppData.icons,
                        selectedIcon = uiState.selectedIcon,
                        selectedColor = uiState.selectedColor,
                        onItemSelected = {
                            if (it is Color) {
                                processIntent(TaskControlIntent.UpdateColor(it))
                            } else {
                                processIntent(TaskControlIntent.UpdateIcon(it as Int))
                            }
                        }
                    )
                }
            )
            HorizontalDivider()
            ListItem(
                headlineContent = {
                    Text(text = "Цвет")
                },
                trailingContent = {
                    GridDialog(
                        items = TaskManAppData.colors,
                        selectedIcon = uiState.selectedIcon,
                        selectedColor = uiState.selectedColor,
                        onItemSelected = {
                            if (it is Color) {
                                processIntent(TaskControlIntent.UpdateColor(it))
                            } else {
                                processIntent(TaskControlIntent.UpdateIcon(it as Int))
                            }
                        }
                    )
                }
            )
            HorizontalDivider()

            TaskTypeDropdownMenu(
                selectedType = uiState.selectedType,
                onTypeSelected = { processIntent(TaskControlIntent.UpdateType(it)) }
            )

            TaskDatePicker(
                onDateSelected = { processIntent(TaskControlIntent.UpdateDate(it)) }
            )
        }
    }
}
