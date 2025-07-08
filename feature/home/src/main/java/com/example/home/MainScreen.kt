package com.example.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.home.sheet.MoveToControl
import com.example.home.task.TaskScreen

@Composable
fun MainScreen(
    mainViewModel: MainViewModel = hiltViewModel(),
    onProfileClick: () -> Unit,
    onTaskControlClick: (Int?) -> Unit,
    onGroupControlClick: (Int?) -> Unit,
    onSearchClick: () -> Unit
) {

    val allGroups by mainViewModel.allGroups.collectAsStateWithLifecycle()
    val mainState by mainViewModel.mainState.collectAsStateWithLifecycle()

    LaunchedEffect(mainState.moveToControl) {
        when (val move = mainState.moveToControl) {
            is MoveToControl.Task -> onTaskControlClick(move.taskId).also {
                mainViewModel.onIntent(MainIntent.MoveTo(MoveToControl.None))
            }
            is MoveToControl.Group -> onGroupControlClick(move.groupId).also {
                mainViewModel.onIntent(MainIntent.MoveTo(MoveToControl.None))
            }
            MoveToControl.None -> Unit
        }
    }

    TaskScreen(
        allGroups = allGroups,
        state = mainState,
        onIntent = mainViewModel::onIntent,
        onProfileClick = onProfileClick,
        onSearchClick = onSearchClick
    )

}

