package com.example.home.sheet

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.shared.UserTaskGroup
import com.example.ui.ColorMapper
import com.example.ui.IconMapper

@Composable
fun GroupTaskDrawerSheet(
    modifier: Modifier = Modifier,
    activeGroupId: Int,
    allGroups: List<UserTaskGroup>,
    onGroupClick: (UserTaskGroup, Boolean) -> Unit,
    onAddClick: () -> Unit,
    onBackClick: () -> Unit
) {
    var isEdit by remember { mutableStateOf(false) }

    ModalDrawerSheet(
        modifier = modifier,
        drawerContainerColor = MaterialTheme.colorScheme.surface
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
                    onCheckedChange = { isEdit = it }
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                }
            }
        ) { paddingValues ->
            GroupTaskDrawerSheetContent(
                modifier = Modifier.padding(paddingValues),
                allGroups = allGroups,
                activeGroupId = activeGroupId,
                onGroupClick = { onGroupClick(it, isEdit) }
            )
        }
    }
}

@Composable
fun GroupTaskDrawerSheetContent(
    modifier: Modifier = Modifier,
    allGroups: List<UserTaskGroup>,
    activeGroupId: Int,
    onGroupClick: (UserTaskGroup) -> Unit
) {
    LazyColumn(modifier = modifier.padding(8.dp)) {
        items(allGroups) { group ->
            NavigationDrawerItem(
                label = { Text(group.name) },
                selected = activeGroupId == group.localId,
                onClick = { onGroupClick(group) },
                icon = {
                    Icon(
                        painter = painterResource(IconMapper.itemIconToDrawable(group.icon)),
                        tint = ColorMapper.mapToColor(group.color),
                        contentDescription = null
                    )
                }
            )
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
