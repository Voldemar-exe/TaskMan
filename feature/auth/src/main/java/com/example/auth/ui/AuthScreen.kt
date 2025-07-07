package com.example.auth.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.auth.AuthIntent
import com.example.auth.AuthMode
import com.example.auth.AuthViewModel
import com.example.auth.isRegister
import com.example.auth.title

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    loginUser: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val authState by viewModel.authState.collectAsStateWithLifecycle()

    LaunchedEffect(authState.error, authState.success) {
        authState.error?.let { error ->
            snackbarHostState.showSnackbar(
                message = error,
                actionLabel = "OK",
                duration = SnackbarDuration.Short
            )
        }
        authState.success?.let { loginUser() }
    }

    BackHandler {
        when (authState.authMode) {
            AuthMode.Register -> onBackClick()
            AuthMode.Login -> viewModel.onIntent(AuthIntent.ToggleMode)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = authState.authMode.title,
                        fontSize = 28.sp
                    )
                }
            )
        },
        bottomBar = {
            AuthBottomBar(
                onBackClick = {
                    if (authState.authMode.isRegister) {
                        onBackClick()
                    } else {
                        viewModel.onIntent(AuthIntent.ToggleMode)
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AuthContent(
                modifier = Modifier.padding(paddingValues),
                authState = authState,
                onIntent = viewModel::onIntent
            )
        }
    }
}


