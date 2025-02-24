package com.example.taskman.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.taskman.model.MyTask

@Preview(showSystemUi = true)
@Composable
fun GroupControl(
    modifier: Modifier = Modifier,
    taskList: List<MyTask> = listOf(MyTask()),
    onAddClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
    ) {

    var selectedColor by remember { mutableStateOf(Color.Red) }
    var selectedIcon by remember { mutableStateOf(Icons.Default.Build) }

    Scaffold(
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = onAddClick) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    Text(text = "Добавить")
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
                value = "",
                label = {
                    Text(text = "Название")
                },
                onValueChange = {}
            )
            ListItem(
                headlineContent = {
                    Text(text = "Иконка")
                },
                trailingContent = {
                    Icon(
                        imageVector = selectedIcon,
                        contentDescription = null
                    )
                }
            )
            HorizontalDivider()
            ListItem(
                headlineContent = {
                    Text(text = "Цвет")
                },
                trailingContent = {
                    // TODO need circle icon
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                }
            )
            HorizontalDivider()
            LazyColumn {
                items(taskList) { task ->
                    TaskItem(
                        task = task,
                        onCheckClick = {}
                    )
                }
            }
        }
    }
}