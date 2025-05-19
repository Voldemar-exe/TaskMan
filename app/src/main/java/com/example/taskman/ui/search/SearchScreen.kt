package com.example.taskman.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.taskman.model.MyTask
import com.example.taskman.ui.components.IntentResult
import com.example.taskman.ui.main.TaskItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    history: List<String>,
    state: SearchState,
    onIntent: (SearchIntent) -> Unit,
    onTaskCheckClick: (MyTask) -> Unit
) {

    Scaffold(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize(),
        topBar = {
            TaskSearchBar(
                state = state,
                onIntent = onIntent,
                searchHistory = history
            )
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
                                    selected = task.isComplete,
                                    task = task,
                                    onCheckClick = onTaskCheckClick
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
