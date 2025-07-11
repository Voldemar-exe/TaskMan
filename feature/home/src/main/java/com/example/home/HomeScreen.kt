package com.example.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.home.content.TaskScreen
import com.example.home.sheet.HomeDestination

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    onProfileClick: () -> Unit,
    onTaskControlClick: (Int?) -> Unit,
    onGroupControlClick: (Int?) -> Unit,
    onSearchClick: () -> Unit
) {

    val allGroups by homeViewModel.allGroups.collectAsStateWithLifecycle()
    val mainState by homeViewModel.mainState.collectAsStateWithLifecycle()

    LaunchedEffect(mainState.homeDestination) {
        when (val move = mainState.homeDestination) {
            is HomeDestination.TaskControl -> onTaskControlClick(move.taskId).also {
                homeViewModel.onIntent(HomeIntent.MoveTo(HomeDestination.Home))
            }
            is HomeDestination.GroupControl -> onGroupControlClick(move.groupId).also {
                homeViewModel.onIntent(HomeIntent.MoveTo(HomeDestination.Home))
            }
            HomeDestination.Home -> Unit
        }
    }

    TaskScreen(
        allGroups = allGroups,
        state = mainState,
        onIntent = homeViewModel::onIntent,
        onProfileClick = onProfileClick,
        onSearchClick = onSearchClick
    )

}

