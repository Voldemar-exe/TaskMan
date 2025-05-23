package com.example.taskman.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.SheetState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.taskman.R
import com.example.taskman.model.MyTask
import com.example.taskman.model.TaskGroup
import com.example.taskman.model.TaskType
import com.example.taskman.ui.control.group.GroupControl
import com.example.taskman.ui.control.group.GroupControlViewModel
import com.example.taskman.ui.control.task.TaskControl
import com.example.taskman.ui.control.task.TaskControlViewModel
import com.example.taskman.ui.theme.Gray
import com.example.taskman.ui.theme.Orange
import com.example.taskman.ui.utils.ItemIcon
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    mainViewModel: MainViewModel,
    onProfileClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    val allTasks by mainViewModel.allTasks.collectAsStateWithLifecycle()
    val allGroups by mainViewModel.allGroups.collectAsStateWithLifecycle()

    val mainState by mainViewModel.uiState.collectAsStateWithLifecycle()

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControlBottomSheet(
    bottomSheetType: MainBottomSheetType,
    allTasks: List<MyTask>,
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

@Composable
fun TaskControlBottomSheetContent(
    taskId: Int?,
    onDismiss: () -> Unit
) {
    val viewModel = koinViewModel<TaskControlViewModel>()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    TaskControl(
        uiState = state,
        onIntent = viewModel::onIntent,
        entityId = taskId,
        onBackClick = onDismiss
    )
}

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskTabs(
    selectedTabIndex: Int,
    onTabSelect: (Int) -> Unit
) {
    val tabs: List<String> = listOf("Все", "Незавершённые", "Завершённые")

    SecondaryTabRow(selectedTabIndex = selectedTabIndex) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onTabSelect(index) },
                text = { Text(text = title, fontSize = 12.sp, maxLines = 1) }
            )
        }
    }
}

private fun getRemainingTimeInfo(taskTimestamp: Long): Pair<String, Color> {
    if (taskTimestamp == 0L) return Pair("Выполнено", Gray)
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val currentDateMidnight = calendar.timeInMillis

    calendar.timeInMillis = taskTimestamp
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val taskDateMidnight = calendar.timeInMillis

    val diffDays = (taskDateMidnight - currentDateMidnight) / (24 * 60 * 60 * 1000)

    return when {
        diffDays < 0 -> Pair("Просрочено", Color.Red)
        diffDays == 0L -> Pair("Сегодня", Orange)
        diffDays == 1L -> Pair("Завтра", Orange)
        diffDays in 2L..4L -> Pair("$diffDays дня", Orange)
        else -> Pair(
            SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(taskDateMidnight),
            Color.Unspecified
        )
    }
}

@Composable
fun TaskItem(
    modifier: Modifier = Modifier,
    task: MyTask,
    selected: Boolean = false,
    isCompleted: Boolean = false,
    onCheckClick: (MyTask) -> Unit
) {
    val (dateText, textColor) = remember(task.date, isCompleted) {
        getRemainingTimeInfo(if (isCompleted) 0L else task.date)
    }

    ListItem(
        modifier = modifier
            .alpha(if (isCompleted) 0.6f else 1f),
        headlineContent = {
            Text(
                text = task.name,
                style = if (isCompleted) {
                    MaterialTheme.typography.bodyLarge.copy(
                        textDecoration = TextDecoration.LineThrough
                    )
                } else {
                    MaterialTheme.typography.bodyLarge
                }
            )
        },
        overlineContent = {
            Text(text = dateText, color = textColor)
        },
        supportingContent = {
            Text(text = TaskType.valueOf(task.type).ru)
        },
        leadingContent = {
            Icon(
                modifier = Modifier.size(36.dp),
                painter = painterResource(ItemIcon.valueOf(task.icon).id),
                tint = Color(task.color),
                contentDescription = null
            )
        },
        trailingContent = {
            RadioButton(
                selected = isCompleted || selected,
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
    selectedTaskTypes: List<TaskType>,
    onSearchClick: () -> Unit,
    onIntent: (MainIntent) -> Unit
) {
    var expandedWorkType by remember { mutableStateOf(false) }

    BottomAppBar(
        modifier = modifier,
        actions = {
            IconButton(onClick = { expandedWorkType = true }) {
                Icon(
                    painter = painterResource(R.drawable.ic_filter),
                    contentDescription = "Filter"
                )
            }
            WorkTypeDropdownMenu(
                expanded = expandedWorkType,
                selectedTaskTypes = selectedTaskTypes,
                onSelectTask = { onIntent(MainIntent.SelectTaskType(it)) },
                onExpandedChange = { expandedWorkType = it }
            )

            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null
                )
            }

            IconButton(onClick = { onIntent(MainIntent.SyncData) }) {
                Icon(
                    painterResource(R.drawable.ic_sync),
                    contentDescription = null
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                onIntent(MainIntent.ShowBottomSheet(MainBottomSheetType.Task()))
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }
        }
    )
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

@Composable
fun WorkTypeDropdownMenu(
    expanded: Boolean,
    selectedTaskTypes: List<TaskType>,
    onSelectTask: (TaskType) -> Unit,
    onExpandedChange: (Boolean) -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { onExpandedChange(false) }
    ) {
        TaskType.entries.forEach { type ->
            DropdownMenuItem(
                text = { Text(text = type.ru) },
                enabled = false,
                onClick = { onSelectTask(type) },
                colors = MenuDefaults.itemColors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface
                ),
                trailingIcon = {
                    Checkbox(
                        checked = type in selectedTaskTypes,
                        onCheckedChange = { onSelectTask(type) }
                    )
                }
            )
        }
    }
}
