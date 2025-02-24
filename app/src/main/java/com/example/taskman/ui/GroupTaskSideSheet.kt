package com.example.taskman.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.taskman.model.TaskGroup

@Preview
@Composable
fun GroupTaskSideSheet(
    modifier: Modifier = Modifier,
    groups: List<TaskGroup> = listOf(TaskGroup(isActive = true))
) {
    ModalDrawerSheet(
        modifier = modifier,

        ) {
        Scaffold(
            topBar = {
                GroupTaskTopBar(
                    onBackClick = {},
                    onAddClick = {}
                )
            },
            bottomBar = {
                HorizontalDivider()
                IconButton(
                    modifier = Modifier.padding(start = 16.dp),
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null
                    )
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(8.dp)
            ) {
                items(groups) { group ->
                    ListItem(
                        headlineContent = {
                            Text(text = group.name)
                        },
                        leadingContent = {
                            // TODO change to icon of group
                           Icon(
                               imageVector = Icons.Default.Build,
                               contentDescription = null
                           )
                        },
                        trailingContent = {
                            if (group.isActive) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupTaskTopBar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onAddClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null
                )
            }
        },
        title = {
            Text("Группы задач")
        },
        actions = {
            IconButton(onClick = onAddClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }
        }
    )
}