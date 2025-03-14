package com.example.taskman.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthenticationScreen(
    viewModel: AuthViewModel = viewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.processIntent(AuthIntent.Back)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(
                    text =
                        if ((uiState as? AuthState.Content)?.isRegister == true) "Регистрация"
                        else "Вход"
                )
            })
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                is AuthState.Loading -> Text("Загрузка...")
                is AuthState.Content -> ContentScreen(
                    state = uiState as AuthState.Content,
                    viewModel = viewModel,
                    paddingValues = paddingValues,
                    onBackClick = onBackClick
                )

                is AuthState.Success -> SuccessScreen((uiState as AuthState.Success).message)
                is AuthState.Error -> ErrorScreen((uiState as AuthState.Error).errorMessage)
            }
        }
    }
}

@Composable
private fun ContentScreen(
    state: AuthState.Content,
    viewModel: AuthViewModel,
    paddingValues: PaddingValues,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = state.login,
            onValueChange = { viewModel.processIntent(AuthIntent.UpdateLogin(it)) },
            label = { Text("Введите логин") },
            leadingIcon = { Icon(Icons.Default.Person, null) },
            trailingIcon = {
                if (state.login.isNotEmpty()) {
                    IconButton(onClick = { viewModel.processIntent(AuthIntent.UpdateLogin("")) }) {
                        Icon(Icons.Default.Clear, null)
                    }
                }
            }
        )
        OutlinedTextField(
            value = state.password,
            onValueChange = { viewModel.processIntent(AuthIntent.UpdatePassword(it)) },
            label = { Text("Введите пароль") },
            leadingIcon = { Icon(Icons.Default.Lock, null) },
            visualTransformation = PasswordVisualTransformation(),
            trailingIcon = {
                if (state.password.isNotEmpty()) {
                    IconButton(onClick = { viewModel.processIntent(AuthIntent.UpdatePassword("")) }) {
                        Icon(Icons.Default.Clear, null)
                    }
                }
            }
        )
        if (state.isRegister) {
            OutlinedTextField(
                value = state.confirmPassword,
                onValueChange = { viewModel.processIntent(AuthIntent.UpdateConfirmPassword(it)) },
                label = { Text("Ещё раз введите пароль") },
                leadingIcon = { Icon(Icons.Default.Lock, null) },
                visualTransformation = PasswordVisualTransformation(),
                trailingIcon = {
                    if (state.confirmPassword.isNotEmpty()) {
                        IconButton(onClick = {
                            viewModel.processIntent(
                                AuthIntent.UpdateConfirmPassword(
                                    ""
                                )
                            )
                        }) {
                            Icon(Icons.Default.Clear, null)
                        }
                    }
                }
            )
        }
        Button(onClick = { viewModel.processIntent(AuthIntent.Submit) }) {
            Text(if (state.isRegister) "Регистрация" else "Вход")
        }
        TextButton(onClick = { viewModel.processIntent(AuthIntent.ToggleMode) }) {
            Text(if (state.isRegister) "Уже есть аккаунт? Войти" else "Зарегистрироваться")
        }
        if (state.error != null) {
            Text(text = state.error, color = MaterialTheme.colorScheme.error)
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = {
            if (state.isRegister) {
                onBackClick()
            } else {
                viewModel.processIntent(AuthIntent.Back)
            }
        }) {
            Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Назад")
            Spacer(modifier = Modifier.width(4.dp))
            Text("Назад")
        }
    }
}

@Composable
private fun SuccessScreen(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = message, style = MaterialTheme.typography.headlineMedium)
    }
}

@Composable
private fun ErrorScreen(errorMessage: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
    }
}