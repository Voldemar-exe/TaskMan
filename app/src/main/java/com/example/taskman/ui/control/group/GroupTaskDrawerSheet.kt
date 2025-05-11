package com.example.taskman.ui.control.group

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.taskman.R
import com.example.taskman.model.TaskGroup

@Composable
fun GroupTaskDrawerSheet(
    modifier: Modifier = Modifier,
    activeGroupId: Int,
    allGroups: List<TaskGroup>,
    onBackClick: () -> Unit = {},
    onAddClick: () -> Unit = {},
    isEdit: Boolean,
    onEditClick: (Boolean) -> Unit,
    onGroupClick: (TaskGroup) -> Unit = {}
) {

    ModalDrawerSheet(
        modifier = modifier
        ) {
        Scaffold(
            topBar = {
                GroupTaskTopBar(
                    onBackClick = onBackClick,
                    onAddClick = onAddClick
                )
            },
            bottomBar = {
                HorizontalDivider()
                IconToggleButton(
                    checked = isEdit,
                    onCheckedChange = onEditClick
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint =
                            if (isEdit) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.secondary
                    )
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(8.dp)
            ) {
                val groups = listOf(
                    TaskGroup(
                        serverId = -1,
                        name = "Все",
                        icon = R.drawable.ic_amount,
                        color = Color.Black.toArgb().toLong()
                    )
                ) + allGroups

                items(groups) { group ->
                    ListItem(
                        modifier = Modifier.clickable{
                            onGroupClick(group)
                        },
                        headlineContent = {
                            Text(text = group.name)
                        },
                        leadingContent = {
                           Icon(
                               painter = painterResource(group.icon),
                               tint = Color(group.color),
                               contentDescription = null
                           )
                        },
                        trailingContent = {
                            if (group.groupId == activeGroupId) {
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