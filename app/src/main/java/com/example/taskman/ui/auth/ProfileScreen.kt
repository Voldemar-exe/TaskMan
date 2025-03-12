package com.example.taskman.ui.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.taskman.model.MyOption

@Preview
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    userName: String = "User",
    options: List<MyOption> = listOf(MyOption()),
    onBackClick: () -> Unit = {}
    ) {
    var isShowInfo by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            ProfileScreenTopBar(
                userName = userName,
                onBackClick = onBackClick,
                onInfoClick = { isShowInfo = true }
            )
        }
    ) { paddingValues ->
        if (isShowInfo)
            InfoDialogScreen(
                onDismissRequest = {
                    isShowInfo = false
                }
            )
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            items(options) { option ->
                OptionElement(
                    selected = option.isActive,
                    onOptionClick = {}
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreenTopBar(
    modifier: Modifier = Modifier,
    userName: String,
    onBackClick: () -> Unit,
    onInfoClick: () -> Unit
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
            Text(text = userName)
        },
        actions = {
            IconButton(onClick = onInfoClick) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null
                )
            }
        }
    )
}

@Composable
fun OptionElement(
    modifier: Modifier = Modifier,
    title: String = "Настройка",
    selected: Boolean,
    onOptionClick: () -> Unit
) {
    ListItem(
        modifier = modifier,
        headlineContent = {
            Text(text = title)
        },
        trailingContent = {
            RadioButton(
                selected = selected,
                onClick = onOptionClick
            )
        }
    )
}

@Composable
fun InfoDialogScreen(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit = {}
) {
    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Card(
            modifier = modifier
                .size(250.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Справка")
                Text(text = "Информация", fontSize = 10.sp)
                TextButton(onClick = onDismissRequest) {
                    Text(text = "Закрыть")
                }
            }
        }
    }
}