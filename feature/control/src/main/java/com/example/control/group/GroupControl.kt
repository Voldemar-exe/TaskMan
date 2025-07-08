package com.example.control.group

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.control.GroupControlIntent
import com.example.control.screen.ControlScreen
import com.example.shared.UserTask
import com.example.ui.components.TaskItem

@Composable
fun GroupControl(
    viewModel: GroupControlViewModel = hiltViewModel(),
    groupId: Int?,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var isChooseMode by remember { mutableStateOf(false) }

    if (!isChooseMode) {
        ControlScreen(
            uiState = uiState,
            onIntent = viewModel::onIntent,
            onBackClick = onBackClick,
            entityId = groupId
        ) {
            uiState.group?.tasksInGroup?.let { selectedTasks ->
                if (selectedTasks.isNotEmpty()) {
                    LazyColumn {
                        items(selectedTasks) { task ->
                            HorizontalDivider()
                            TaskItem(
                                task = task,
                                selected = true,
                                onCheckClick = {
                                    viewModel.onIntent(GroupControlIntent.RemoveTask(task))
                                }
                            )
                        }
                    }
                }
            }

            TextButton(onClick = { isChooseMode = true }) {
                Text("Выбрать задачи")
            }
        }
    } else {
        TasksForGroupScreen(
            allTasks = emptyList(), // TODO ADD ALL TASKS FROM SOMEWHERE
            tasksInGroup = uiState.group?.tasksInGroup,
            onDismissRequest = { isChooseMode = false },
            onAddTask = {
                viewModel.onIntent(
                    GroupControlIntent.AddTask(it),
                )
            },
            onRemoveTask = {
                viewModel.onIntent(
                    GroupControlIntent.RemoveTask(it),
                )
            }
        )
    }
}

@Composable
fun TasksForGroupScreen(
    allTasks: List<UserTask>,
    tasksInGroup: List<UserTask>?,
    onDismissRequest: () -> Unit,
    onAddTask: (UserTask) -> Unit,
    onRemoveTask: (UserTask) -> Unit
) {
    Scaffold(
        bottomBar = {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                TextButton(onClick = onDismissRequest) {
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
        LazyColumn(Modifier.padding(paddingValues)) {
            items(allTasks) { task ->
                TaskItem(
                    task = task,
                    selected = (tasksInGroup)?.contains(task) == true,
                    onCheckClick = {
                        if ((tasksInGroup)?.contains(task) == true) {
                            onRemoveTask(task)
                        } else {
                            onAddTask(task)
                        }
                    }
                )
            }
        }
    }
}
