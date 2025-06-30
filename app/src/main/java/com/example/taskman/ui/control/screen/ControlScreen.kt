package com.example.taskman.ui.control.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.taskman.ui.components.IntentResult
import com.example.taskman.ui.control.ControlIntent
import com.example.taskman.ui.control.ControlState

@Composable
fun ControlScreen(
    uiState: ControlState,
    onIntent: (ControlIntent) -> Unit,
    entityId: Int?,
    onBackClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val sbHostState = remember { SnackbarHostState() }

    LaunchedEffect(entityId) {
        if (entityId != null) {
            onIntent(ControlIntent.LoadEntity(entityId))
        }
    }

    LaunchedEffect(uiState.base.intentRes) {
        when (uiState.base.intentRes) {
            IntentResult.None -> Unit
            is IntentResult.Error -> {
                sbHostState.showSnackbar(
                    message = uiState.base.intentRes.message ?: "Ошибка",
                    actionLabel = "OK",
                    duration = SnackbarDuration.Short
                )
                onIntent(ControlIntent.ClearError)
            }

            is IntentResult.Success -> {
                if (uiState.base.intentRes.message == ControlIntent.SaveEntity.toString()) {
                    onIntent(ControlIntent.ClearState)
                    onBackClick()
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(sbHostState) },
        topBar = {
            uiState.base.entityId?.let {
                ControlTopBar(
                    onDelete = {
                        onIntent(ControlIntent.DeleteEntity(it))
                        onBackClick()
                        onIntent(ControlIntent.ClearState)
                    }
                )
            }
        },
        bottomBar = {
            ControlBottomBar(
                isEdit = entityId != null,
                onIntent = onIntent,
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        ControlContent(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            uiState = uiState.base,
            onIntent = onIntent,
            content = content
        )
    }
}

