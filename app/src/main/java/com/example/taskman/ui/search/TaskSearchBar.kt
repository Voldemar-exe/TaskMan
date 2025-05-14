package com.example.taskman.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.FlowPreview

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun TaskSearchBar(
    modifier: Modifier = Modifier,
    state: SearchState,
    searchHistory: List<String>,
    onIntent: (SearchIntent) -> Unit,
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
    ) {
        SearchInputField(
            query = state.inputText,
            expandedSearch = state.expandedSearch,
            onFocusClear = { focusManager.clearFocus() },
            onIntent = onIntent
        )
        if (state.expandedSearch) {
            SearchHistoryContent(
                searchHistory = searchHistory,
                onFocusClear = { focusManager.clearFocus() },
                onIntent = onIntent
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchInputField(
    query: String,
    expandedSearch: Boolean,
    onFocusClear: () -> Unit,
    onIntent: (SearchIntent) -> Unit
) = OutlinedTextField(
    modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .onFocusEvent {
            onIntent(SearchIntent.OnExpandedChange(it.isFocused))
        },
    value = query,
    onValueChange = { onIntent(SearchIntent.ChangeInputText(it)) },
    placeholder = { Text("Поиск задачи...") },
    leadingIcon = {
        if (expandedSearch) {
            IconButton(
                onClick = {
                    onFocusClear()
                    onIntent(SearchIntent.OnExpandedChange(false))
                }
            ) {
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
    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
    keyboardActions = KeyboardActions(
        onSearch = {
            onFocusClear()
            onIntent(SearchIntent.Search)
            onIntent(SearchIntent.OnExpandedChange(false))
        }
    ),
    maxLines = 1
)

@Composable
fun SearchHistoryContent(
    searchHistory: List<String>,
    onFocusClear: () -> Unit,
    onIntent: (SearchIntent) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(searchHistory) { searchedText ->
            Row(
                modifier = Modifier
                    .clickable {
                        onFocusClear()
                        onIntent(SearchIntent.ChangeInputText(searchedText))
                        onIntent(SearchIntent.Search)
                    }
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "HistoryItem"
                )
                Text(searchedText)
            }
            HorizontalDivider()
        }
        if (searchHistory.isNotEmpty()) {
            item {
                TextButton(onClick = { onIntent(SearchIntent.ClearSearchHistory) }) {
                    Text("Удалить историю")
                }
            }
        }
    }
}
