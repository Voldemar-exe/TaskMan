package com.example.home.sheet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.control.group.GroupControl
import com.example.control.group.GroupControlViewModel
import com.example.shared.UserTask

@Composable
fun GroupControlBottomSheetContent(
    groupId: Int?,
    viewModel: GroupControlViewModel = hiltViewModel(),
    allTasks: List<UserTask>,
    onDismiss: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    GroupControl(
        uiState = state,
        allTasks = allTasks,
        onIntent = viewModel::onIntent,
        onBackClick = onDismiss,
        entityId = groupId
    )
}