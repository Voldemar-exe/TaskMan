package com.example.control.screen

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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import com.example.control.ControlIntent
import com.example.control.ControlState
import com.example.shared.ItemColor
import com.example.shared.UserIcon
import com.example.ui.components.ColorGridDialog
import com.example.ui.components.IconGridDialog

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

        ControlListItemToDialog(label = "Иконка") {
            IconGridDialog(
                icons = UserIcon.entries,
                selectedIcon = uiState.selectedIcon,
                selectedColor = uiState.selectedColor,
                onIconSelected = { onIntent(ControlIntent.UpdateIcon(it)) }
            )
        }

        ControlListItemToDialog(label = "Цвет") {
            ColorGridDialog(
                colors = ItemColor.entries,
                selectedColor = uiState.selectedColor,
                onColorSelected = { onIntent(ControlIntent.UpdateColor(it)) }
            )
        }

        content()
    }
}

@Composable
fun ControlListItemToDialog(
    label: String,
    dialog: @Composable () -> Unit
) {
    ListItem(
        headlineContent = { Text(text = label) },
        trailingContent = {
            dialog()
        }
    )
    HorizontalDivider()
}