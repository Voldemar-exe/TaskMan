package com.example.taskman.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthenticationScreen(
    viewModel: AuthViewModel = koinViewModel(),
    onBackClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            scope.launch {
                snackbarHostState.showSnackbar("Snackbar")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(
                    text =
                        if (uiState.isRegister) "Регистрация"
                        else "Вход"
                )
            })
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            ContentScreen(
                modifier = Modifier.padding(paddingValues),
                uiState = uiState,
                processIntent = viewModel::processIntent,
                onBackClick = onBackClick
            )
        }
    }
}

@Composable
private fun ContentScreen(
    modifier: Modifier = Modifier,
    uiState: AuthState,
    processIntent: (AuthIntent) -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = uiState.login,
            onValueChange = { processIntent(AuthIntent.UpdateLogin(it)) },
            label = { Text("Введите логин") },
            leadingIcon = { Icon(Icons.Default.Person, null) },
            trailingIcon = {
                if (uiState.login.isNotEmpty()) {
                    IconButton(
                        onClick = { processIntent(AuthIntent.UpdateLogin("")) }
                    ) {
                        Icon(Icons.Default.Clear, null)
                    }
                }
            }
        )
        OutlinedTextField(
            value = uiState.password,
            onValueChange = { processIntent(AuthIntent.UpdatePassword(it)) },
            label = { Text("Введите пароль") },
            leadingIcon = { Icon(Icons.Default.Lock, null) },
            visualTransformation = PasswordVisualTransformation(),
            trailingIcon = {
                if (uiState.password.isNotEmpty()) {
                    IconButton(
                        onClick = { processIntent(AuthIntent.UpdatePassword("")) }
                    ) {
                        Icon(Icons.Default.Clear, null)
                    }
                }
            }
        )
        if (uiState.isRegister) {
            OutlinedTextField(
                value = uiState.confirmPassword,
                onValueChange = { processIntent(AuthIntent.UpdateConfirmPassword(it)) },
                label = { Text("Ещё раз введите пароль") },
                leadingIcon = { Icon(Icons.Default.Lock, null) },
                visualTransformation = PasswordVisualTransformation(),
                trailingIcon = {
                    if (uiState.confirmPassword.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                processIntent(AuthIntent.UpdateConfirmPassword(""))
                            }
                        ) {
                            Icon(Icons.Default.Clear, null)
                        }
                    }
                }
            )
        }
        Button(onClick = { processIntent(AuthIntent.Submit) }) {
            Text(if (uiState.isRegister) "Регистрация" else "Вход")
        }
        TextButton(onClick = { processIntent(AuthIntent.ToggleMode) }) {
            Text(if (uiState.isRegister) "Уже есть аккаунт? Войти" else "Зарегистрироваться")
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = {
            if (!uiState.isRegister) {
                onBackClick()
            } else {
                processIntent(AuthIntent.ToggleMode)
            }
        }) {
            Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Назад")
            Spacer(modifier = Modifier.width(4.dp))
            Text("Назад")
        }
    }
}

@Composable
private fun ErrorScreen(errorMessage: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = errorMessage)
    }
}