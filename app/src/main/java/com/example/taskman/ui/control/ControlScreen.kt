package com.example.taskman.ui.control

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.taskman.ui.components.GridDialog
import com.example.taskman.ui.components.IntentResult
import com.example.taskman.ui.utils.ItemIcon
import com.example.taskman.ui.utils.TaskManAppData

@Composable
fun ControlScreen(
    uiState: ControlState,
    onIntent: (ControlIntent) -> Unit,
    entityId: Int?,
    onBackClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(entityId) {
        if (entityId != null) {
            onIntent(ControlIntent.LoadEntity(entityId))
        }
    }

    LaunchedEffect(uiState.base.intentRes) {
        when (uiState.base.intentRes) {
            IntentResult.None -> Unit
            is IntentResult.Error -> {
                snackbarHostState.showSnackbar(
                    message = uiState.base.intentRes.message ?: "Ошибка",
                    actionLabel = "OK",
                    duration = SnackbarDuration.Short
                )
                onIntent(ControlIntent.ClearError)
            }

            is IntentResult.Success -> {
                if (uiState.base.intentRes.message == ControlIntent.SaveEntity.toString()) {
                    onIntent(ControlIntent.ClearState)
                    onBackClick()
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                uiState.base.entityId?.let {
                    IconButton(
                        onClick = {
                            onIntent(ControlIntent.DeleteEntity(it))
                            onBackClick()
                            onIntent(ControlIntent.ClearState)
                        }
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete"
                        )
                    }
                }
            }
        },
        bottomBar = {
            ControlBottomBar(
                isEdit = entityId != null,
                onIntent = onIntent,
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        ControlContent(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            uiState = uiState.base,
            onIntent = onIntent,
            content = content
        )
    }
}

@Composable
fun ControlBottomBar(
    isEdit: Boolean,
    onIntent: (ControlIntent) -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                onIntent(ControlIntent.SaveEntity)
            }
        ) {
            Icon(
                imageVector = Icons.Default.AddCircle,
                contentDescription = null
            )
            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
            Text(text = if (!isEdit) "Добавить" else "Сохранить")
        }
        TextButton(
            onClick = {
                onIntent(ControlIntent.ClearState)
                onBackClick()
            }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null
            )
            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
            Text(text = "Назад")
        }
    }
}

@Composable
fun ControlContent(
    modifier: Modifier = Modifier,
    uiState: ControlState.BaseState,
    onIntent: (ControlIntent) -> Unit,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.entityName,
            onValueChange = { onIntent(ControlIntent.UpdateName(it)) },
            label = { Text("Название") }
        )
        ListItem(
            headlineContent = {
                Text(text = "Иконка")
            },
            trailingContent = {
                GridDialog(
                    items = TaskManAppData.icons,
                    selectedIcon = ItemIcon.valueOf(uiState.selectedIcon).id,
                    selectedColor = uiState.selectedColor,
                    onItemSelected = { onIntent(onDialogChoose(it)) }
                )
            }
        )
        HorizontalDivider()
        ListItem(
            headlineContent = {
                Text(text = "Цвет")
            },
            trailingContent = {
                GridDialog(
                    items = TaskManAppData.colors,
                    selectedIcon = ItemIcon.valueOf(uiState.selectedIcon).id,
                    selectedColor = uiState.selectedColor,
                    onItemSelected = { onIntent(onDialogChoose(it)) }
                )
            }
        )
        HorizontalDivider()
        content()
    }
}

private fun onDialogChoose(item: Any): ControlIntent {
    return if (item is Color) {
        ControlIntent.UpdateColor(item)
    } else {
        ControlIntent.UpdateIcon(ItemIcon.entries.find { it.id == item as Int }?.name ?: "Work")
    }
}
