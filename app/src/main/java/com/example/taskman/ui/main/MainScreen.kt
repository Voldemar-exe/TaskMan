package com.example.taskman.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.taskman.R
import com.example.taskman.model.ItemIcon
import com.example.taskman.model.MyTask
import com.example.taskman.model.TaskGroup
import com.example.taskman.model.TaskType
import com.example.taskman.ui.control.TaskControlIntent
import com.example.taskman.ui.control.group.GroupControl
import com.example.taskman.ui.control.group.GroupControlViewModel
import com.example.taskman.ui.control.group.GroupTaskDrawerSheet
import com.example.taskman.ui.control.task.TaskControl
import com.example.taskman.ui.control.task.TaskControlViewModel
import com.example.taskman.ui.theme.Orange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    mainViewModel: MainViewModel,
    taskControlViewModel: TaskControlViewModel,
    groupControlViewModel: GroupControlViewModel,
    onProfileClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    val allTasks by mainViewModel.allTasks.collectAsStateWithLifecycle()
    val allGroups by mainViewModel.allGroups.collectAsStateWithLifecycle()

    val mainState by mainViewModel.uiState.collectAsStateWithLifecycle()
    val taskControlState by taskControlViewModel.uiState.collectAsStateWithLifecycle()
    val groupControlState by groupControlViewModel.uiState.collectAsStateWithLifecycle()

    val sheetState: SheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val scope: CoroutineScope = rememberCoroutineScope()

    LaunchedEffect(mainState.bottomSheet) {
        if (mainState.bottomSheet == MainBottomSheetType.None) {
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) {
                    mainViewModel.onIntent(MainIntent.CloseBottomSheet)
                }
            }
        }
    }

    TaskScreen(
        allGroups = allGroups,
        state = mainState,
        onIntent = mainViewModel::onIntent,
        onProfileClick = onProfileClick,
        onSearchClick = onSearchClick,
        onCheckClick = {
            mainViewModel.onIntent(MainIntent.ToggleTaskCompletion(it))
            taskControlViewModel.onIntent(
                TaskControlIntent.UpdateTaskToServer(
                    it.copy(isComplete = !it.isComplete)
                )
            )
        }
    )

    when (val sheet = mainState.bottomSheet) {
        MainBottomSheetType.None -> Unit
        is MainBottomSheetType.Task -> {
            ModalBottomSheet(
                onDismissRequest = { mainViewModel.onIntent(MainIntent.CloseBottomSheet) },
                sheetState = sheetState
            ) {
                TaskControl(
                    uiState = taskControlState,
                    onIntent = taskControlViewModel::onIntent,
                    entityId = sheet.taskId,
                    onBackClick = { mainViewModel.onIntent(MainIntent.CloseBottomSheet) }
                )
            }
        }

        is MainBottomSheetType.Group -> {
            ModalBottomSheet(
                onDismissRequest = { mainViewModel.onIntent(MainIntent.CloseBottomSheet) },
                sheetState = sheetState
            ) {
                GroupControl(
                    uiState = groupControlState,
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    allGroups: List<TaskGroup>,
    state: MainState,
    onIntent: (MainIntent) -> Unit,
    onProfileClick: () -> Unit,
    onSearchClick: () -> Unit,
    onCheckClick: (MyTask) -> Unit
) {
    val drawerState: DrawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerContent = {
            GroupTaskDrawerSheet(
                onIntent = onIntent,
                allGroups = allGroups,
                activeGroupId = state.selectedGroupId,
                isEdit = state.isGroupEditMode,
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
                    onSelectTask = { onIntent(MainIntent.SelectTaskType(it)) },
                    onSearchClick = onSearchClick,
                    onAddClick = {
                        onIntent(MainIntent.ShowBottomSheet(MainBottomSheetType.Task()))
                    }
                )
            }
        ) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
                HorizontalDivider()
                if (state.selectedGroupId != -2) {
                    TaskTabs(
                        selectedTabIndex = state.selectedTabIndex,
                        onTabSelect = { onIntent(MainIntent.SelectTab(it)) }
                    )
                }
                LazyColumn {
                    items(state.visibleTasks) { task ->
                        TaskItem(
                            modifier = Modifier.clickable {
                                onIntent(
                                    MainIntent.ShowBottomSheet(
                                        MainBottomSheetType.Task(task.taskId)
                                    )
                                )
                            },
                            task = task,
                            onCheckClick = onCheckClick
                        )
                    }
                }
            }
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
    selectedTaskTypes: List<TaskType>,
    onSelectTask: (TaskType) -> Unit,
    onSearchClick: () -> Unit,
    onAddClick: () -> Unit
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
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null
                )
            }
            WorkTypeDropdownMenu(
                expanded = expandedWorkType,
                selectedTaskTypes = selectedTaskTypes,
                onSelectTask = onSelectTask,
                onExpandedChange = { expandedWorkType = it }
            )
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
