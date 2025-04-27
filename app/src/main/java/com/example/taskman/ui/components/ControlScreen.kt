package com.example.taskman.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import com.example.taskman.ui.utils.TaskManAppData

@Composable
fun ControlScreen(
    modifier: Modifier = Modifier,
    uiState: ControlState,
    processIntent: (ControlIntent) -> Unit,
    entityId: Int?,
    onBackClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(entityId) {
        if (entityId != null) {
            processIntent(ControlIntent.LoadEntity(entityId))
        } else {
            processIntent(ControlIntent.ClearState)
        }
    }

    LaunchedEffect(uiState.base.intentRes) {
        when (uiState.base.intentRes) {
            IntentResult.None -> null
            is IntentResult.Error -> {
                snackbarHostState.showSnackbar(
                    message = uiState.base.intentRes.message ?: "Ошибка",
                    actionLabel = "OK",
                    duration = SnackbarDuration.Short
                )
                processIntent(ControlIntent.ClearError)
            }

            is IntentResult.Success -> {
                if (uiState.base.intentRes.message == ControlIntent.SaveEntity.toString()) {
                    processIntent(ControlIntent.ClearState)
                    onBackClick()
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        processIntent(ControlIntent.SaveEntity)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    Text(text = if (entityId == null) "Добавить" else "Сохранить")
                }
                TextButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    Text(text = "Назад")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.base.entityName,
                onValueChange = { processIntent(ControlIntent.UpdateName(it)) },
                label = { Text("Название") }
            )
            ListItem(
                headlineContent = {
                    Text(text = "Иконка")
                },
                trailingContent = {
                    GridDialog(
                        items = TaskManAppData.icons,
                        selectedIcon = uiState.base.selectedIcon,
                        selectedColor = uiState.base.selectedColor,
                        onItemSelected = {
                            if (it is Color) {
                                processIntent(ControlIntent.UpdateColor(it))
                            } else {
                                processIntent(ControlIntent.UpdateIcon(it as Int))
                            }
                        }
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
                        selectedIcon = uiState.base.selectedIcon,
                        selectedColor = uiState.base.selectedColor,
                        onItemSelected = {
                            if (it is Color) {
                                processIntent(ControlIntent.UpdateColor(it))
                            } else {
                                processIntent(ControlIntent.UpdateIcon(it as Int))
                            }
                        }
                    )
                }
            )
            HorizontalDivider()
            content()
        }
    }
}
