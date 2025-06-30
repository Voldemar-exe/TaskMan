package com.example.taskman.ui.main.task

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.taskman.model.MyTask
import com.example.taskman.model.TaskGroup
import com.example.taskman.ui.main.MainIntent
import com.example.taskman.ui.main.MainState
import com.example.taskman.ui.main.sheet.GroupTaskDrawerSheet
import com.example.taskman.ui.main.sheet.MainBottomSheetType
import kotlinx.coroutines.launch

@Composable
fun TaskScreen(
    allGroups: List<TaskGroup>,
    state: MainState,
    onIntent: (MainIntent) -> Unit,
    onProfileClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    val drawerState: DrawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerContent = {
            GroupTaskDrawerSheet(
                activeGroupId = state.selectedGroupId,
                allGroups = allGroups,
                onGroupClick = { group, isEdit ->
                    if (isEdit) {
                        onIntent(
                            MainIntent.ShowBottomSheet(MainBottomSheetType.Group(group.groupId))
                        )
                    } else {
                        isLoading = true
                        scope.launch {
                            drawerState.close()
                            onIntent(MainIntent.SelectGroup(group))
                        }.invokeOnCompletion {
                            isLoading = false
                        }
                    }
                },
                onAddClick = {
                    onIntent(
                        MainIntent.ShowBottomSheet(MainBottomSheetType.Group())
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
            },
            bottomBar = {
                TaskScreenBottomBar(
                    selectedTaskTypes = state.selectedTaskTypes.toList(),
                    onIntent = onIntent,
                    onSearchClick = onSearchClick
                )
            }
        ) { paddingValues ->
            TaskScreenList(
                modifier = Modifier.padding(paddingValues),
                selectedTabIndex = state.selectedTabIndex,
                tasks = state.visibleTasks,
                isLoading = isLoading,
                onIntent = onIntent
            )
        }
    }
}

@Composable
fun TaskScreenList(
    modifier: Modifier = Modifier,
    selectedTabIndex: Int,
    tasks: List<MyTask>,
    isLoading: Boolean,
    onIntent: (MainIntent) -> Unit
) {
    Column(modifier = modifier) {
        HorizontalDivider()
        TaskTabs(
            selectedTabIndex = selectedTabIndex,
            onTabSelect = { onIntent(MainIntent.SelectTab(it)) }
        )
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn {
                items(tasks) { task ->
                    TaskItem(
                        modifier = Modifier.clickable {
                            onIntent(
                                MainIntent.ShowBottomSheet(
                                    MainBottomSheetType.Task(task.taskId)
                                )
                            )
                        },
                        isCompleted = task.isComplete,
                        task = task,
                        onCheckClick = { onIntent(MainIntent.ToggleTaskCompletion(it)) }
                    )
                }
            }
        }
    }
}