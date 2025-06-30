package com.example.taskman.ui.control.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import com.example.taskman.ui.components.GridDialog
import com.example.taskman.ui.control.ControlIntent
import com.example.taskman.ui.control.ControlState
import com.example.taskman.ui.utils.ItemIcon
import com.example.taskman.ui.utils.TaskManAppData

@Composable
fun ControlContent(
    modifier: Modifier = Modifier,
    uiState: ControlState.BaseState,
    onIntent: (ControlIntent) -> Unit,
    content: @Composable () -> Unit
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.entityName,
            onValueChange = { onIntent(ControlIntent.UpdateName(it)) },
            label = { Text("Название") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                }
            )
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