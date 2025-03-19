package com.example.taskman.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskman.model.MyTask
import com.example.taskman.ui.group.GroupControl
import com.example.taskman.ui.group.GroupTaskDrawerSheet
import com.example.taskman.ui.task.TaskControl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class SampleTaskProvider : PreviewParameterProvider<MyTask> {
    override val values = sequenceOf(
        MyTask()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun TaskScreen(
    modifier: Modifier = Modifier,
    groupName: String = "Test",
    viewModel: MainViewModel = viewModel(),
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    scope: CoroutineScope = rememberCoroutineScope(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    onProfileClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.bottomSheet) {
        if (uiState.bottomSheet == MainState.BottomSheetType.None) {
            sheetState.hide()
        }
    }

    ModalNavigationDrawer(
        drawerContent = {
            GroupTaskDrawerSheet(
                onBackClick = {
                    drawerControl(scope, drawerState)
                },
                onAddClick = {
                    viewModel.processIntent(
                        MainIntent.ShowBottomSheet(MainState.BottomSheetType.Task)
                    )
                },
                onGroupClick = {
                    viewModel.processIntent(
                        MainIntent.ShowBottomSheet(
                            MainState.BottomSheetType.Group,
                            true
                        )
                    )
                }
            )
        },
        drawerState = drawerState
    ) {
        Scaffold(
            modifier = modifier,
            topBar = {
                TaskScreenTopBar(
                    groupName = groupName,
                    onMenuClick = {
                        drawerControl(scope, drawerState)
                    },
                    onProfileClick = onProfileClick
                )
            },
            bottomBar = {
                TaskScreenBottomBar(
                    onAddClick = {
                        viewModel.processIntent(
                            MainIntent.ShowBottomSheet(MainState.BottomSheetType.Group)
                        )
                    },
                    onSearchClick = {}
                )
            }
        ) { paddingValues ->
            if (uiState.bottomSheet == MainState.BottomSheetType.Task) {
                ModalBottomSheet(
                    onDismissRequest = {
                        viewModel.processIntent(MainIntent.CloseBottomSheet)
                    },
                    sheetState = sheetState
                ) {
                    TaskControl(
                        onBackClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    viewModel.processIntent(MainIntent.CloseBottomSheet)
                                }
                            }
                        },
                        isEdit = uiState.isEditMode
                    )
                }
            }
            if (uiState.bottomSheet == MainState.BottomSheetType.Group) {
                ModalBottomSheet(
                    onDismissRequest = {
                        viewModel.processIntent(MainIntent.CloseBottomSheet)
                    },
                    sheetState = sheetState
                ) {
                    GroupControl(
                        onBackClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    viewModel.processIntent(MainIntent.CloseBottomSheet)
                                }
                            }
                        },
                        isEdit = false
                    )
                }
            }
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
            ) {
                items(uiState.tasks) { task ->
                    TaskItem(
                        modifier = Modifier.clickable {
                            viewModel.processIntent(
                                MainIntent.ShowBottomSheet(
                                    MainState.BottomSheetType.Task,
                                    true
                                )
                            )
                        },
                        task = task,
                        onCheckClick = viewModel::processIntent
                    )
                }
            }
        }
    }
}

private fun drawerControl(
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
                imageVector = Icons.Default.Build,
                contentDescription = null
            )
        },
        trailingContent = {
            RadioButton(
                selected = task.isComplete,
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