package com.example.taskman.ui.control.group

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.taskman.model.TaskGroup
import com.example.taskman.ui.main.MainBottomSheetType
import com.example.taskman.ui.main.MainIntent
import com.example.taskman.ui.utils.ItemIcon

@Composable
fun GroupTaskDrawerSheet(
    modifier: Modifier = Modifier,
    onIntent: (MainIntent) -> Unit,
    activeGroupId: Int,
    allGroups: List<TaskGroup>,
    isEdit: Boolean,
    onBackClick: () -> Unit
) {
    ModalDrawerSheet(modifier = modifier) {
        Scaffold(
            topBar = {
                GroupTaskTopBar(
                    onBackClick = {
                        onIntent(MainIntent.LoadTasks)
                        onBackClick()
                    },
                    onAddClick = {
                        onIntent(MainIntent.ShowBottomSheet(MainBottomSheetType.Group()))
                    }
                )
            },
            bottomBar = {
                HorizontalDivider()
                IconToggleButton(
                    checked = isEdit,
                    onCheckedChange = { onIntent(MainIntent.ChangeEditMode(it)) }
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
                items(allGroups) { group ->
                    NavigationDrawerItem(
                        label = { Text(group.name) },
                        selected = activeGroupId == group.groupId,
                        onClick = {
                            if (isEdit) {
                                if (group.groupId > 0) {
                                    onIntent(
                                        MainIntent.ShowBottomSheet(
                                            MainBottomSheetType.Group(group.groupId)
                                        )
                                    )
                                }
                            } else {
                                onIntent(MainIntent.SelectGroup(group))
                                onBackClick()
                            }
                        },
                        icon = {
                            Icon(
                                painter = painterResource(ItemIcon.valueOf(group.icon).id),
                                tint = Color(group.color),
                                contentDescription = null
                            )
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
