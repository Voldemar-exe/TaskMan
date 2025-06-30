package com.example.taskman.ui.main.sheet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.taskman.model.MyTask
import com.example.taskman.ui.control.group.GroupControl
import com.example.taskman.ui.control.group.GroupControlViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun GroupControlBottomSheetContent(
    groupId: Int?,
    allTasks: List<MyTask>,
    onDismiss: () -> Unit
) {
    val viewModel = koinViewModel<GroupControlViewModel>()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    GroupControl(
        uiState = state,
        allTasks = allTasks,
        onIntent = viewModel::onIntent,
        onBackClick = onDismiss,
        entityId = groupId
    )
}