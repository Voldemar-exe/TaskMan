package com.example.taskman.ui.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthenticationScreen(viewModel: AuthViewModel) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.processIntent(AuthIntent.Back)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(text = if ((state as? AuthState.Content)?.isRegister == true) "Регистрация" else "Вход")
            })
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (val currentState = state) {
                is AuthState.Loading -> Text("Загрузка...")
                is AuthState.Content -> ContentScreen(currentState, viewModel, paddingValues)
                is AuthState.Success -> SuccessScreen(currentState.message)
                is AuthState.Error -> ErrorScreen(currentState.errorMessage)
            }
        }
    }
}

@Composable
private fun ContentScreen(
    state: AuthState.Content,
    viewModel: AuthViewModel,
    paddingValues: PaddingValues
) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = state.login,
            onValueChange = { viewModel.processIntent(AuthIntent.UpdateLogin(it)) },
            label = { Text("Введите логин") },
            leadingIcon = { Icon(Icons.Default.Person, null) }
        )
        OutlinedTextField(
            value = state.password,
            onValueChange = { viewModel.processIntent(AuthIntent.UpdatePassword(it)) },
            label = { Text("Введите пароль") },
            leadingIcon = { Icon(Icons.Default.Lock, null) }
        )
        if (state.isRegister) {
            OutlinedTextField(
                value = state.confirmPassword,
                onValueChange = { viewModel.processIntent(AuthIntent.UpdateConfirmPassword(it)) },
                label = { Text("Введите пароль снова") },
                leadingIcon = { Icon(Icons.Default.Lock, null) }
            )
        }
        Button(onClick = { viewModel.processIntent(AuthIntent.Submit) }) {
            Text(if (state.isRegister) "Регистрация" else "Вход")
        }
        TextButton(onClick = { viewModel.processIntent(AuthIntent.ToggleMode) }) {
            Text(if (state.isRegister) "Уже есть аккаунт? Войти" else "Нет аккаунта? Зарегистрироваться")
        }
        if (state.error != null) {
            Text(text = state.error, color = MaterialTheme.colorScheme.error)
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