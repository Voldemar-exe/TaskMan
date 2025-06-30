package com.example.taskman.ui.main

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.taskman.ui.main.sheet.ControlBottomSheet
import com.example.taskman.ui.main.sheet.MainBottomSheetType
import com.example.taskman.ui.main.task.TaskScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    mainViewModel: MainViewModel,
    onProfileClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    val allTasks by mainViewModel.allTasks.collectAsStateWithLifecycle()
    val allGroups by mainViewModel.allGroups.collectAsStateWithLifecycle()

    val mainState by mainViewModel.mainState.collectAsStateWithLifecycle()

    val scope: CoroutineScope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState: SheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    LaunchedEffect(mainState.bottomSheet) {
        if (mainState.bottomSheet !is MainBottomSheetType.None) {
            showBottomSheet = true
            scope.launch { sheetState.show() }
        }
    }

    TaskScreen(
        allGroups = allGroups,
        state = mainState,
        onIntent = mainViewModel::onIntent,
        onProfileClick = onProfileClick,
        onSearchClick = onSearchClick
    )

    if (showBottomSheet) {
        ControlBottomSheet(
            bottomSheetType = mainState.bottomSheet,
            allTasks = allTasks,
            sheetState = sheetState,
            onDismiss = {
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    mainViewModel.onIntent(MainIntent.CloseBottomSheet)
                    showBottomSheet = false
                }
            }
        )
    }
}

