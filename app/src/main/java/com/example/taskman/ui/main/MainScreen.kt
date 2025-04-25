package com.example.taskman.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.taskman.model.MyTask
import com.example.taskman.ui.group.GroupControl
import com.example.taskman.ui.group.GroupControlViewModel
import com.example.taskman.ui.group.GroupTaskDrawerSheet
import com.example.taskman.ui.task.TaskControl
import com.example.taskman.ui.task.TaskControlViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = koinViewModel<MainViewModel>(),
    taskControlViewModel: TaskControlViewModel = koinViewModel<TaskControlViewModel>(),
    groupControlViewModel: GroupControlViewModel = koinViewModel<GroupControlViewModel>(),
    onProfileClick: () -> Unit = {},
    onSearchClick: () -> Unit = {}
) {
    val allTasks by mainViewModel.allTasks.collectAsStateWithLifecycle()
    val allGroups by mainViewModel.allGroups.collectAsStateWithLifecycle()

    val mainUiState by mainViewModel.uiState.collectAsStateWithLifecycle()
    val taskControlUiState by taskControlViewModel.uiState.collectAsStateWithLifecycle()
    val groupControlUiState by groupControlViewModel.uiState.collectAsStateWithLifecycle()

    val sheetState: SheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val scope: CoroutineScope = rememberCoroutineScope()
    val drawerState: DrawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )

    LaunchedEffect(mainUiState.selectedGroupId) {
        mainViewModel.processIntent(MainIntent.LoadTasks)
    }

    LaunchedEffect(mainUiState.bottomSheet) {
        if (mainUiState.bottomSheet == MainState.BottomSheetType.None) {
            sheetState.hide()
        }
    }

    ModalNavigationDrawer(
        drawerContent = {
            GroupTaskDrawerSheet(
                onBackClick = {
                    mainViewModel.processIntent(MainIntent.LoadTasks)
                    drawerToggle(scope, drawerState)
                },
                onAddClick = {
                    mainViewModel.processIntent(
                        MainIntent.ShowBottomSheet(MainState.BottomSheetType.Group())
                    )
                },
                onGroupClick = {
                    if (mainUiState.isGroupEditMode) {
                        mainViewModel.processIntent(
                            MainIntent.ShowBottomSheet(
                                MainState.BottomSheetType.Group(it.groupId)
                            )
                        )
                    } else {
                        mainViewModel.processIntent(
                            MainIntent.SelectGroup(it)
                        )
                        drawerToggle(scope, drawerState)
                    }
                },
                allGroups = allGroups,
                activeGroupId = mainUiState.selectedGroupId,
                isEdit = mainUiState.isGroupEditMode,
                onEditClick = { mainViewModel.processIntent(MainIntent.ChangeEditMode(it)) }
            )
        },
        drawerState = drawerState
    ) {
        Scaffold(
            modifier = modifier,
            topBar = {
                TaskScreenTopBar(
                    groupName = mainUiState.selectedGroupName,
                    onMenuClick = {
                        drawerToggle(scope, drawerState)
                    },
                    onProfileClick = onProfileClick
                )
            },
            bottomBar = {
                TaskScreenBottomBar(
                    onAddClick = {
                        mainViewModel.processIntent(
                            MainIntent.ShowBottomSheet(MainState.BottomSheetType.Task())
                        )
                    },
                    onSearchClick = onSearchClick
                )
            }
        ) { paddingValues ->
            when (val sheet = mainUiState.bottomSheet) {
                MainState.BottomSheetType.None -> null
                is MainState.BottomSheetType.Task -> {
                    ModalBottomSheet(
                        onDismissRequest = {
                            mainViewModel.processIntent(MainIntent.CloseBottomSheet)
                        },
                        sheetState = sheetState
                    ) {
                        TaskControl(
                            uiState = taskControlUiState,
                            processIntent = taskControlViewModel::processIntent,
                            onBackClick = {
                                scope.launch { sheetState.hide() }.invokeOnCompletion {
                                    if (!sheetState.isVisible) {
                                        mainViewModel.processIntent(MainIntent.CloseBottomSheet)
                                    }
                                }
                            },
                            taskId = sheet.taskId
                        )
                    }
                }

                is MainState.BottomSheetType.Group -> {
                    ModalBottomSheet(
                        onDismissRequest = {
                            mainViewModel.processIntent(MainIntent.CloseBottomSheet)
                        },
                        sheetState = sheetState
                    ) {
                        GroupControl(
                            uiState = groupControlUiState,
                            processIntent = groupControlViewModel::processIntent,
                            onBackClick = {
                                scope.launch { sheetState.hide() }.invokeOnCompletion {
                                    if (!sheetState.isVisible) {
                                        mainViewModel.processIntent(MainIntent.CloseBottomSheet)
                                    }
                                }
                            },
                            allTasks = allTasks,
                            groupId = sheet.groupId
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
            ) {
                items(mainUiState.tasks) { task ->
                    TaskItem(
                        modifier = Modifier.clickable {
                            mainViewModel.processIntent(
                                MainIntent.ShowBottomSheet(
                                    MainState.BottomSheetType.Task(task.taskId)
                                )
                            )
                        },
                        task = task,
                        onCheckClick = mainViewModel::processIntent
                    )
                }
            }
        }
    }
}

private fun drawerToggle(
    scope: CoroutineScope,
    drawerState: DrawerState
) {
    scope.launch {
        if (drawerState.isClosed) {
            drawerState.open()
        } else {
            drawerState.close()
        }
    }
}

@Composable
fun TaskItem(
    modifier: Modifier = Modifier,
    task: MyTask,
    selected: Boolean = false,
    onCheckClick: (MainIntent.MainSwitch) -> Unit
) {
    ListItem(
        modifier = modifier,
        headlineContent = {
            Text(text = task.name)
        },
        overlineContent = {
            Text(text = task.type)
        },
        supportingContent = {
            Text(text = task.note)
        },
        leadingContent = {
            Icon(
                painter = painterResource(task.icon),
                tint = Color(task.color),
                contentDescription = null
            )
        },
        trailingContent = {
            RadioButton(
                selected = task.isComplete || selected,
                onClick = { onCheckClick(MainIntent.MainSwitch(task)) }
            )
        }
    )
    HorizontalDivider()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreenTopBar(
    modifier: Modifier = Modifier,
    groupName: String,
    onMenuClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = null
                )
            }
        },
        title = {
            Text(text = groupName)
        },
        actions = {
            IconButton(onClick = onProfileClick) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null
                )
            }
        }
    )
}

@Composable
fun TaskScreenBottomBar(
    modifier: Modifier = Modifier,
    onSearchClick: () -> Unit,
    onAddClick: () -> Unit
) {

    BottomAppBar(
        modifier = modifier,
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }
        }
    )
}