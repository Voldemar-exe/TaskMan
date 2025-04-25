package com.example.taskman.ui.group

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.taskman.model.MyTask
import com.example.taskman.ui.components.GridDialog
import com.example.taskman.ui.main.TaskItem
import com.example.taskman.ui.utils.TaskManAppData

@Composable
fun GroupControl(
    modifier: Modifier = Modifier,
    uiState: GroupControlState,
    processIntent: (GroupControlIntent) -> Unit,
    allTasks: List<MyTask>,
    groupId: Int?,
    onBackClick: () -> Unit
) {
    LaunchedEffect(groupId) {
        if (groupId != null) {
            processIntent(GroupControlIntent.LoadGroup(groupId))
        } else {
            processIntent(GroupControlIntent.ClearGroup)
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
                Button(onClick = {
                    processIntent(GroupControlIntent.SaveGroup)
                    onBackClick()
                }) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    Text(text = if (groupId == null) "Добавить" else "Сохранить")
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
                value = uiState.groupName,
                onValueChange = { processIntent(GroupControlIntent.UpdateName(it)) },
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
                                processIntent(GroupControlIntent.UpdateColor(it))
                            } else {
                                processIntent(GroupControlIntent.UpdateIcon(it as Int))
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
                                processIntent(GroupControlIntent.UpdateColor(it))
                            } else {
                                processIntent(GroupControlIntent.UpdateIcon(it as Int))
                            }
                        }
                    )
                }
            )
            HorizontalDivider()
            LazyColumn {
                items(
                    if (groupId == null) allTasks
                    else uiState.tasksInGroup
                ) { task ->
                    TaskItem(
                        task = task,
                        selected = uiState.tasksInGroup.contains(task),
                        onCheckClick = {
                            if (uiState.tasksInGroup.contains(task)) {
                                processIntent(GroupControlIntent.RemoveTask(task))
                            } else {
                                processIntent(GroupControlIntent.AddTask(task))
                            }
                        }
                    )
                }
            }
        }
    }
}