package com.example.taskman.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
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
import com.example.taskman.ui.utils.OptionViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    userName: String = "User",
    profileViewModel: ProfileViewModel = koinViewModel(),
    optionViewModel: OptionViewModel = koinViewModel(),
    onBackClick: () -> Unit
) {

    val profileState by profileViewModel.uiState.collectAsStateWithLifecycle()
    val isDarkTheme by optionViewModel.isDarkTheme.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            ProfileScreenTopBar(
                userName = userName,
                onBackClick = onBackClick,
                onExitClick = {
                    profileViewModel.onIntent(ProfileIntent.ClearProfile)
                    onBackClick()
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            item {
                OptionElement(
                    title = "Темная тема",
                    selected = isDarkTheme,
                    onOptionClick = { optionViewModel.toggleTheme() }
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
    onExitClick: () -> Unit
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
            IconButton(onClick = onExitClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ExitToApp,
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