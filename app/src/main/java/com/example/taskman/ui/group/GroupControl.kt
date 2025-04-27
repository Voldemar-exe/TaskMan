package com.example.taskman.ui.group

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.example.taskman.model.MyTask
import com.example.taskman.ui.components.ControlIntent
import com.example.taskman.ui.components.ControlScreen
import com.example.taskman.ui.components.ControlState
import com.example.taskman.ui.components.GroupControlIntent
import com.example.taskman.ui.main.TaskItem

@Composable
fun GroupControl(
    uiState: ControlState,
    entityId: Int?,
    allTasks: List<MyTask>,
    processIntent: (ControlIntent) -> Unit,
    onBackClick: () -> Unit
) {
    ControlScreen(
        uiState = uiState,
        processIntent = processIntent,
        onBackClick = onBackClick,
        entityId = entityId
    ) {
        uiState.group?.let { groupState ->
            LazyColumn {
                items(
                    if (entityId == null) allTasks
                    else groupState.tasksInGroup
                ) { task ->
                    TaskItem(
                        task = task,
                        selected = groupState.tasksInGroup.contains(task),
                        onCheckClick = {
                            if (groupState.tasksInGroup.contains(task)) {
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