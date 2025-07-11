package com.example.home.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.home.HomeIntent
import com.example.home.sheet.HomeDestination
import com.example.shared.SystemIcon
import com.example.shared.TaskType
import com.example.ui.IconMapper
import com.example.ui.components.WorkTypeDropdownMenu

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TaskScreenBottomBar(
    modifier: Modifier = Modifier,
    selectedTaskTypes: List<TaskType>,
    isShowAllTasks: Boolean,
    onSearchClick: () -> Unit,
    onEditClick: () -> Unit,
    onIntent: (HomeIntent) -> Unit
) {
    var expandedWorkType by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        HorizontalFloatingToolbar(
            expanded = true,
            floatingActionButton = {
                FloatingToolbarDefaults.VibrantFloatingActionButton(
                    onClick = {
                        onIntent(HomeIntent.MoveTo(HomeDestination.TaskControl()))
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add"
                    )
                }
            }
        ) {
            if (!isShowAllTasks) {
                IconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit"
                    )
                }
            }

            IconButton(onClick = { expandedWorkType = true }) {
                Icon(
                    painter = painterResource(IconMapper.itemIconToDrawable(SystemIcon.Filter)),
                    contentDescription = "Filter"
                )
            }
            WorkTypeDropdownMenu(
                expanded = expandedWorkType,
                selectedTaskTypes = selectedTaskTypes,
                onSelectTask = { onIntent(HomeIntent.SelectTaskType(it)) },
                onExpandedChange = { expandedWorkType = it }
            )

            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            }

            IconButton(onClick = { onIntent(HomeIntent.SyncData) }) {
                Icon(
                    painterResource(IconMapper.itemIconToDrawable(SystemIcon.Sync)),
                    contentDescription = "Sync"
                )
            }
        }
    }
}