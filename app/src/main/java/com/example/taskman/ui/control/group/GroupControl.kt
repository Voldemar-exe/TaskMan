package com.example.taskman.ui.control.group

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.taskman.model.MyTask
import com.example.taskman.ui.control.ControlIntent
import com.example.taskman.ui.control.ControlScreen
import com.example.taskman.ui.control.ControlState
import com.example.taskman.ui.control.GroupControlIntent
import com.example.taskman.ui.main.TaskItem

@Composable
fun GroupControl(
    uiState: ControlState,
    allTasks: List<MyTask>,
    entityId: Int?,
    onIntent: (ControlIntent) -> Unit,
    onBackClick: () -> Unit
) {
    var isChooseMode by remember { mutableStateOf(false) }

    if (!isChooseMode) {
        ControlScreen(
            uiState = uiState,
            onIntent = onIntent,
            onBackClick = onBackClick,
            entityId = entityId
        ) {
            uiState.group?.tasksInGroup?.let { selectedTasks ->
                if (selectedTasks.isNotEmpty()) {
                    LazyColumn {
                        items(selectedTasks) { task ->
                            TaskItem(
                                task = task,
                                selected = true,
                                onCheckClick = {
                                    onIntent(GroupControlIntent.RemoveTask(task))
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
            allTasks = allTasks,
            tasksInGroup = uiState.group?.tasksInGroup,
            onDismissRequest = { isChooseMode = false },
            onAddTask = {
                onIntent(
                    GroupControlIntent.AddTask(it),
                )
            },
            onRemoveTask = {
                onIntent(
                    GroupControlIntent.RemoveTask(it),
                )
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksForGroupScreen(
    allTasks: List<MyTask>,
    tasksInGroup: List<MyTask>?,
    onDismissRequest: () -> Unit,
    onAddTask: (MyTask) -> Unit,
    onRemoveTask: (MyTask) -> Unit
) {

//    BottomSheetScaffold() { }

    Scaffold(
        bottomBar = {
            TextButton(onClick = onDismissRequest) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                Text(text = "Назад")
            }
        }
    ) { paddingValues ->
        LazyColumn(Modifier.padding(paddingValues)) {
            items(allTasks) { task ->
                TaskItem(
                    task = task,
                    selected = (tasksInGroup)?.contains(task) == true,
                    onCheckClick = { task ->
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
