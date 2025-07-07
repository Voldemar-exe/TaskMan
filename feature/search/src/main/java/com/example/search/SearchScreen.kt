package com.example.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shared.IntentResult
import com.example.ui.components.TaskItem

@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val state by searchViewModel.state.collectAsStateWithLifecycle()

    SearchContent(
        state = state,
        onIntent = searchViewModel::onIntent,
        onBackClick = onBackClick
    )
}

@Composable
fun SearchContent(
    state: SearchState,
    onIntent: (SearchIntent) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize(),
        topBar = {
            TaskSearchBar(
                state = state,
                onIntent = onIntent
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                TextButton(onClick = onBackClick) {
                    Icon(
                        Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                    Text("Назад")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) CircularProgressIndicator()
            when (state.result) {
                IntentResult.None -> NoResultsPlaceholder()

                is IntentResult.Error -> ErrorPlaceholder(state.result.message ?: "Ошибка") {
                    onIntent(SearchIntent.Search)
                }

                is IntentResult.Success -> {
                    if (!state.expandedSearch) {
                        LazyColumn {
                            items(state.searchedTasks) { task ->
                                TaskItem(
                                    isCompleted = task.isComplete,
                                    task = task,
                                    onCheckClick = {
                                        onIntent(SearchIntent.ToggleTaskCompletion(it))
                                        onIntent(SearchIntent.Search)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NoResultsPlaceholder() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "Ничего не найдено",
            style = MaterialTheme.typography.bodyLarge.copy(color = Color.Gray)
        )
    }
}

@Composable
fun ErrorPlaceholder(errorMessage: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Ошибка: $errorMessage",
            color = Color.Red,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Повторить попытку")
        }
    }
}
