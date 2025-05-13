package com.example.taskman.ui.search

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.FlowPreview


// TODO FIX THIS SOMEHOW
@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun TaskSearchBar(
    modifier: Modifier = Modifier,
    state: SearchState,
    searchHistory: List<String>,
    onIntent: (SearchIntent) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(state.expandedTaskList) {
        Log.i("SEARCH", "$state")
    }


    SearchBar(
        modifier = modifier
            .fillMaxWidth(),
        inputField = {
            SearchInputField(
                query = state.inputText,
                expandedTaskList = state.expandedTaskList,
                onIntent = onIntent,
                interactionSource = interactionSource
            )
        },
        expanded = state.expandedTaskList,
        onExpandedChange = { onIntent(SearchIntent.OnExpandedChange(it)) }
    ) {
        SearchHistoryContent(
            searchHistory = searchHistory,
            onIntent = onIntent
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchInputField(
    query: String,
    expandedTaskList: Boolean,
    interactionSource: MutableInteractionSource,
    onIntent: (SearchIntent) -> Unit
) = SearchBarDefaults.InputField(
    query = query,
    onQueryChange = { onIntent(SearchIntent.ChangeInputText(it)) },
    onSearch = { onIntent(SearchIntent.Search) },
    placeholder = { Text("Поиск задачи...") },
    leadingIcon = {
        if (expandedTaskList) {
            IconButton(onClick = {
                onIntent(SearchIntent.OnExpandedChange(false))
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
        } else {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search"
            )
        }
    },
    trailingIcon = {
        if (!query.isEmpty()) {
            IconButton(onClick = { onIntent(SearchIntent.ClearSearchQuery) }) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear"
                )
            }
        }
    },
    expanded = expandedTaskList,
    onExpandedChange = { onIntent(SearchIntent.OnExpandedChange(it)) },
    interactionSource = interactionSource
)

@Composable
fun SearchHistoryContent(
    searchHistory: List<String>,
    onIntent: (SearchIntent) -> Unit
) {
    Scaffold(
        floatingActionButton = {
            SmallFloatingActionButton(
                onClick = { onIntent(SearchIntent.OnExpandedChange(false)) }
            ) {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(searchHistory) { searchedText ->
                ListItem(
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "HistoryItem"
                        )
                    },
                    headlineContent = { Text(searchedText) },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    modifier = Modifier
                        .clickable {
                            onIntent(SearchIntent.ChangeInputText(searchedText))
                            onIntent(SearchIntent.Search)
                        }
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }
            if (searchHistory.isNotEmpty()) {
                item {
                    HorizontalDivider()
                    TextButton(onClick = { onIntent(SearchIntent.ClearSearchHistory) }) {
                        Text("Удалить историю")
                    }
                }
            }
        }
    }
}
