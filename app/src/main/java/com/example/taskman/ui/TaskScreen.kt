package com.example.taskman.ui

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
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.taskman.model.MyTask
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
    taskList: List<MyTask> = listOf(
        MyTask(),
        MyTask("Test2"),
        MyTask("Test3"),
        MyTask("Test4"),
    ),
    onCheckClick: (MyTask) -> Unit = {},
    onProfileClick: () -> Unit = {}
) {

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var showEdit by remember { mutableStateOf(false) }
    var showTaskBottomSheet by remember { mutableStateOf(false) }
    var showGroupBottomSheet by remember { mutableStateOf(false) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    ModalNavigationDrawer(
        drawerContent = {
            GroupTaskDrawerSheet(
                onBackClick = {
                    drawerControl(scope, drawerState)
                },
                onAddClick = {
                    showGroupBottomSheet = true
                },
                onGroupClick = {
                    showEdit = true
                    showGroupBottomSheet = true
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
                    onAddClick = { showTaskBottomSheet = true },
                    onSearchClick = {}
                )
            }
        ) { paddingValues ->
            if (showTaskBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showTaskBottomSheet = false
                        showEdit = false
                    },
                    sheetState = sheetState
                ) {
                    TaskControl(
                        onBackClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    showTaskBottomSheet = false
                                    showEdit = false
                                }
                            }
                        },
                        isEdit = showEdit
                    )
                }
            }
            if (showGroupBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showGroupBottomSheet = false
                        showEdit = false
                    },
                    sheetState = sheetState
                ) {
                    GroupControl(
                        onBackClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    showGroupBottomSheet = false
                                    showEdit = false
                                }
                            }
                        },
                        isEdit = showEdit
                    )
                }
            }
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
            ) {
                items(taskList) { task ->
                    TaskItem(
                        modifier = Modifier.clickable {
                            showEdit = true
                            showTaskBottomSheet = true
                        },
                        task = task,
                        onCheckClick = onCheckClick
                    )
                }
            }

            var text by rememberSaveable { mutableStateOf("") }

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
    onCheckClick: (MyTask) -> Unit
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

@OptIn(ExperimentalMaterial3Api::class)
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