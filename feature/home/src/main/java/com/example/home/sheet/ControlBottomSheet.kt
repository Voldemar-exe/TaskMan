package com.example.home.sheet

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import com.example.shared.UserTask

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControlBottomSheet(
    bottomSheetType: MainBottomSheetType,
    allTasks: List<UserTask>,
    sheetState: SheetState,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        when (bottomSheetType) {
            MainBottomSheetType.None -> Unit
            is MainBottomSheetType.Task -> {
                TaskControlBottomSheetContent(
                    taskId = bottomSheetType.taskId,
                    onDismiss = onDismiss
                )
            }

            is MainBottomSheetType.Group -> {
                GroupControlBottomSheetContent(
                    groupId = bottomSheetType.groupId,
                    allTasks = allTasks,
                    onDismiss = onDismiss
                )
            }
        }
    }
}