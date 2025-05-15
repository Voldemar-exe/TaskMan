package com.example.taskman.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.taskman.model.MyTask
import com.example.taskman.model.TaskTypes
import com.example.taskman.ui.control.TaskControlIntent
import com.example.taskman.ui.control.group.GroupControl
import com.example.taskman.ui.control.group.GroupControlViewModel
import com.example.taskman.ui.control.group.GroupTaskDrawerSheet
import com.example.taskman.ui.control.task.TaskControl
import com.example.taskman.ui.control.task.TaskControlViewModel
import com.example.taskman.ui.theme.Orange
import com.example.taskman.ui.utils.TaskManAppData.icons
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
    taskControlViewModel: TaskControlViewModel,
    groupControlViewModel: GroupControlViewModel,
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
        mainViewModel.onIntent(MainIntent.LoadTasks)
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
                    mainViewModel.onIntent(MainIntent.LoadTasks)
                    drawerToggle(scope, drawerState)
                },
                onAddClick = {
                    mainViewModel.onIntent(
                        MainIntent.ShowBottomSheet(MainState.BottomSheetType.Group())
                    )
                },
                onGroupClick = {
                    if (mainUiState.isGroupEditMode) {
                        mainViewModel.onIntent(
                            MainIntent.ShowBottomSheet(
                                MainState.BottomSheetType.Group(it.groupId)
                            )
                        )
                    } else {
                        mainViewModel.onIntent(
                            MainIntent.SelectGroup(it)
                        )
                        drawerToggle(scope, drawerState)
                    }
                },
                allGroups = allGroups,
                activeGroupId = mainUiState.selectedGroupId,
                isEdit = mainUiState.isGroupEditMode,
                onEditClick = { mainViewModel.onIntent(MainIntent.ChangeEditMode(it)) }
            )
        },
        drawerState = drawerState
    ) {
        Scaffold(
            modifier = modifier,
            topBar = {
                TaskScreenTopBar(
                    groupName = mainUiState.selectedGroupName,
                    onMenuClick = { drawerToggle(scope, drawerState) },
                    onProfileClick = onProfileClick
                )
            },
            bottomBar = {
                TaskScreenBottomBar(
                    onAddClick = {
                        mainViewModel.onIntent(
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
                            mainViewModel.onIntent(MainIntent.CloseBottomSheet)
                        },
                        sheetState = sheetState
                    ) {
                        TaskControl(
                            uiState = taskControlUiState,
                            onIntent = taskControlViewModel::onIntent,
                            entityId = sheet.taskId,
                            onBackClick = {
                                scope.launch { sheetState.hide() }.invokeOnCompletion {
                                    if (!sheetState.isVisible) {
                                        mainViewModel.onIntent(MainIntent.CloseBottomSheet)
                                    }
                                }
                            }
                        )
                    }
                }

                is MainState.BottomSheetType.Group -> {
                    ModalBottomSheet(
                        onDismissRequest = {
                            mainViewModel.onIntent(MainIntent.CloseBottomSheet)
                        },
                        sheetState = sheetState
                    ) {
                        GroupControl(
                            uiState = groupControlUiState,
                            allTasks = allTasks,
                            onIntent = groupControlViewModel::onIntent,
                            onBackClick = {
                                scope.launch { sheetState.hide() }.invokeOnCompletion {
                                    if (!sheetState.isVisible) {
                                        mainViewModel.onIntent(MainIntent.LoadTasks)
                                        mainViewModel.onIntent(MainIntent.CloseBottomSheet)
                                    }
                                }
                            },
                            entityId = sheet.groupId
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
            ) {
                items(mainUiState.tasks.sortedBy { it.taskId }.reversed()) { task ->
                    TaskItem(
                        modifier = Modifier.clickable {
                            mainViewModel.onIntent(
                                MainIntent.ShowBottomSheet(
                                    MainState.BottomSheetType.Task(task.taskId)
                                )
                            )
                        },
                        task = task,
                        onCheckClick = {
                            mainViewModel.onIntent(
                                MainIntent.MainSwitch(it)
                            )
                            taskControlViewModel.onIntent(
                                TaskControlIntent.UpdateTaskToServer(
                                    task.copy(isComplete = !task.isComplete)
                                )
                            )
                        }
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

private fun getRemainingTimeInfo(taskTimestamp: Long): Pair<String, Color> {
    val currentTime = System.currentTimeMillis()
    val diffMillis = taskTimestamp - currentTime
    val daysLeft = TimeUnit.MILLISECONDS.toDays(diffMillis)

    return when {
        diffMillis < 0 -> Pair("Просрочено", Color.Red)
        daysLeft == 0L -> Pair("Сегодня", Orange)
        daysLeft == 1L -> Pair("Завтра", Orange)
        daysLeft in 2L..4L -> Pair("$daysLeft дня", Orange)
        daysLeft in 3L..6L -> Pair("$daysLeft дней", Color.Unspecified)
        else -> Pair(
            SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(taskTimestamp),
            Color.Unspecified
        )
    }
}

@Composable
fun TaskItem(
    modifier: Modifier = Modifier,
    task: MyTask,
    selected: Boolean = false,
    onCheckClick: (MyTask) -> Unit
) {
    val (dateText, textColor) = remember(task.date) {
        getRemainingTimeInfo(task.date)
    }

    ListItem(
        modifier = modifier,
        headlineContent = {
            Text(text = task.name)
        },
        overlineContent = {
            Text(text = dateText, color = textColor)
        },
        supportingContent = {
            Text(text = TaskTypes.valueOf(task.type).ru)
        },
        leadingContent = {
            // TODO STORE ICON IN DB IN DIFFERENT WAY
            if (task.icon in icons) {
                Icon(
                    modifier = Modifier.size(36.dp),
                    painter = painterResource(task.icon),
                    tint = Color(task.color),
                    contentDescription = null
                )
            }
        },
        trailingContent = {
            RadioButton(
                selected = task.isComplete || selected,
                onClick = { onCheckClick(task) }
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