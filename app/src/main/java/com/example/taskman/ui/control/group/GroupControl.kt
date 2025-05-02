package com.example.taskman.ui.control.group

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.taskman.model.MyTask
import com.example.taskman.ui.control.ControlIntent
import com.example.taskman.ui.control.ControlScreen
import com.example.taskman.ui.control.ControlState
import com.example.taskman.ui.control.GroupControlIntent
import com.example.taskman.ui.main.TaskItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupControl(
    uiState: ControlState,
    allTasks: List<MyTask>,
    entityId: Int?,
    processIntent: (ControlIntent) -> Unit,
    onBackClick: () -> Unit
) {

    val sheetState: SheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val scope: CoroutineScope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    ControlScreen(
        uiState = uiState,
        processIntent = processIntent,
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
                                processIntent(GroupControlIntent.RemoveTask(task))
                            }
                        )
                    }
                }
            }
        }

        TextButton(
            onClick = {
                showBottomSheet = true
                scope.launch {
                    sheetState.show()
                }
            }
        ) {
            Text("Выбрать задачи")
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                modifier = Modifier.fillMaxSize(),
                onDismissRequest = {
                    scope.launch {
                        sheetState.hide()
                        showBottomSheet = false
                    }
                },
                sheetState = sheetState
            ) {
                LazyColumn {
                    items(allTasks) { task ->
                        TaskItem(
                            task = task,
                            selected = (uiState.group?.tasksInGroup ?: emptyList()).contains(task),
                            onCheckClick = { task ->
                                if ((uiState.group?.tasksInGroup ?: emptyList()).contains(task)) {
                                    processIntent(
                                        GroupControlIntent.RemoveTask(task)
                                    )
                                } else {
                                    processIntent(
                                        GroupControlIntent.AddTask(task)
                                    )
                                }
                            }
                        )
                    }
                }
                TextButton(
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                            showBottomSheet = false
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    Text(text = "Назад")
                }
            }
        }
    }
}