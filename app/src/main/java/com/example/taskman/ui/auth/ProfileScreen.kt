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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.taskman.ui.utils.ThemeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    userName: String = "User",
    themeViewModel: ThemeViewModel = koinViewModel(),
    viewModel: ProfileViewModel = koinViewModel(),
//    options: List<MyOption> = emptyList(),
    onBackClick: () -> Unit = {}
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val currentState = uiState) {
        is ProfileState.Content ->
            Scaffold(
                topBar = {
                    ProfileScreenTopBar(
                        userName = userName,
                        onBackClick = onBackClick,
                        onInfoClick = {
                            viewModel.processIntent(ProfileIntent.InfoClick)
                        }
                    )
                }
            ) { paddingValues ->
                if (currentState.isInfo)
                    InfoDialogScreen(
                        onDismissRequest = {
                            viewModel.processIntent(ProfileIntent.InfoClick)
                        }
                    )
                LazyColumn(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {
                    items(currentState.options) { option ->
                        OptionElement(
                            title = option.name,
                            selected = option.isActive,
                            onOptionClick = {
                                viewModel.processIntent(
                                    ProfileIntent.OptionClick(option)
                                )
                                themeViewModel.processOption(option)
                            }
                        )
                    }
                }
            }

        ProfileState.Loading -> CircularProgressIndicator()
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
    title: String,
    selected: Boolean,
    onOptionClick: (Boolean) -> Unit
) {
    ListItem(
        modifier = modifier,
        headlineContent = {
            Text(text = title)
        },
        trailingContent = {
            Switch(
                checked = selected,
                onCheckedChange = onOptionClick
            )
        }
    )
}

@Composable
fun InfoDialogScreen(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit
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