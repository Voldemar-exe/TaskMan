package com.example.control.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.control.ControlIntent

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
        Button(onClick = { onIntent(ControlIntent.SaveEntity) }) {
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