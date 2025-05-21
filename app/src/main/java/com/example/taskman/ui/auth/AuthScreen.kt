package com.example.taskman.ui.auth

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.taskman.navigation.Profile
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: AuthViewModel = koinViewModel(),
    onBackClick: () -> Unit,
    loginUser: (Profile) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.error, uiState.success) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(
                message = uiState.error ?: "Ошибка",
                actionLabel = "OK",
                duration = SnackbarDuration.Short
            )
        }
        uiState.success?.let { loginUser(Profile(uiState.login)) }
    }

    BackHandler {
        when (uiState.authMode) {
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
                        text = uiState.authMode.title,
                        fontSize = 28.sp
                    )
                }
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                TextButton(
                    onClick = {
                        if (uiState.authMode.isRegister) {
                            onBackClick()
                        } else {
                            viewModel.onIntent(AuthIntent.ToggleMode)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "Назад"
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Назад")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AuthContentScreen(
                modifier = Modifier.padding(paddingValues),
                uiState = uiState,
                onIntent = viewModel::onIntent
            )
        }
    }
}

@Composable
private fun AuthContentScreen(
    modifier: Modifier = Modifier,
    uiState: AuthState,
    onIntent: (AuthIntent) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AuthTextField(
            title = "Введите логин",
            value = uiState.login,
            imageVector = Icons.Default.Person,
            isPasswordField = false,
            onValueChange = { onIntent(AuthIntent.UpdateLogin(it)) }
        )
        AuthTextField(
            title = "Введите пароль",
            value = uiState.password,
            imageVector = Icons.Default.Lock,
            isPasswordField = true,
            onValueChange = { onIntent(AuthIntent.UpdatePassword(it)) }
        )

        if (uiState.authMode.isRegister) {
            AuthTextField(
                title = "Ещё раз введите пароль",
                value = uiState.confirmPassword,
                imageVector = Icons.Default.Lock,
                isPasswordField = true,
                onValueChange = { onIntent(AuthIntent.UpdateConfirmPassword(it)) }
            )
        }

        Button(onClick = { onIntent(AuthIntent.Submit) }) {
            Text(uiState.authMode.title)
        }

        TextButton(onClick = { onIntent(AuthIntent.ToggleMode) }) {
            Text(uiState.authMode.backButtonText)
        }
    }
}

@Composable
fun AuthTextField(
    title: String,
    value: String,
    imageVector: ImageVector,
    isPasswordField: Boolean,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        value = value,
        onValueChange = onValueChange,
        label = { Text(title) },
        leadingIcon = { Image(imageVector, contentDescription = null) },
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = { onValueChange("") }) {
                    Icon(Icons.Default.Clear, null)
                }
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType =
                if (isPasswordField) {
                    KeyboardType.Password
                } else {
                    KeyboardType.Text
                }
        ),
        visualTransformation =
            if (isPasswordField) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
        }
    )
}
