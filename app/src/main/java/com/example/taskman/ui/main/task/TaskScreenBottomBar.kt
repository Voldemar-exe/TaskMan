package com.example.taskman.ui.main.task

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.taskman.R
import com.example.taskman.model.TaskType
import com.example.taskman.ui.components.WorkTypeDropdownMenu
import com.example.taskman.ui.main.MainIntent
import com.example.taskman.ui.main.sheet.MainBottomSheetType

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