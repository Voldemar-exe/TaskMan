package com.example.taskman.ui

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.taskman.model.MyTask

class SampleTaskProvider: PreviewParameterProvider<MyTask> {
    override val values = sequenceOf(
        MyTask()
    )
}

@Preview
@Composable
fun TaskScreen(
    modifier: Modifier = Modifier,
    groupName: String = "Test",
    taskList: List<MyTask> = listOf(MyTask()),
    onCheckClick: (MyTask) -> Unit = {}
) {
    Scaffold(
        topBar = {
            TaskScreenTopBar(
                groupName = groupName,
                onMenuClick = {},
                onProfileClick = {}
            )
        },
        bottomBar = {
            TaskScreenBottomBar(
                onAddClick = {},
                onSearchClick = {}
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .padding(paddingValues)
        ) {
            items(taskList) { task ->
                TaskItem(
                    task = task,
                    onCheckClick = onCheckClick
                )
            }
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