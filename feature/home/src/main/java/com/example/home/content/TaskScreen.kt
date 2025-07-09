package com.example.home.content

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.home.HomeIntent
import com.example.home.HomeState
import com.example.home.sheet.GroupTaskDrawerSheet
import com.example.home.sheet.MoveToControl
import com.example.shared.UserTask
import com.example.shared.UserTaskGroup
import com.example.ui.components.TaskItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TaskScreen(
    allGroups: List<UserTaskGroup>,
    state: HomeState,
    onIntent: (HomeIntent) -> Unit,
    onProfileClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    val drawerState: DrawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerContent = {
            GroupTaskDrawerSheet(
                activeGroupId = state.selectedGroupId,
                allGroups = allGroups,
                onGroupClick = { group ->
                    scope.launch {
                        delay(100L)
                        onIntent(HomeIntent.SelectGroup(group))
                        delay(400L)
                        drawerState.close()
                    }
                },
                onAddClick = {
                    onIntent(
                        HomeIntent.MoveTo(MoveToControl.Group())
                    )
                },
                onBackClick = { scope.launch { drawerState.close() } }
            )
        },
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = {
                TaskScreenTopBar(
                    groupName = state.selectedGroupName,
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onProfileClick = onProfileClick
                )
            }
        ) { paddingValues ->
            TaskScreenContent(
                modifier = Modifier.padding(paddingValues),
                selectedTabIndex = state.selectedTabIndex,
                tasks = state.visibleTasks,
                onIntent = onIntent
            )
            Box(Modifier.fillMaxSize()) {
                TaskScreenBottomBar(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    selectedTaskTypes = state.selectedTaskTypes.toList(),
                    isShowAllTasks = state.selectedGroupId == -1,
                    onIntent = onIntent,
                    onSearchClick = onSearchClick,
                    onEditClick = {
                        onIntent(
                            HomeIntent.MoveTo(MoveToControl.Group(state.selectedGroupId))
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun TaskScreenContent(
    modifier: Modifier = Modifier,
    selectedTabIndex: Int,
    tasks: List<UserTask>,
    onIntent: (HomeIntent) -> Unit
) {
    Column(modifier = modifier) {
        HorizontalDivider()
        TaskTabs(
            selectedTabIndex = selectedTabIndex,
            onTabSelect = { onIntent(HomeIntent.SelectTab(it)) }
        )
        LazyColumn {
            items(tasks) { task ->
                TaskItem(
                    modifier = Modifier.clickable {
                        onIntent(
                            HomeIntent.MoveTo(
                                MoveToControl.Task(task.localId)
                            )
                        )
                    },
                    isCompleted = task.isComplete,
                    task = task,
                    onCheckClick = { onIntent(HomeIntent.ToggleTaskCompletion(it)) }
                )
            }
        }
    }
}