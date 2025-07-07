package com.example.auth.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.clearText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.auth.AuthIntent
import com.example.auth.AuthState
import com.example.auth.backButtonText
import com.example.auth.isRegister
import com.example.auth.title

@Composable
fun AuthContent(
    modifier: Modifier = Modifier,
    authState: AuthState,
    onIntent: (AuthIntent) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AuthInputFields(
            modifier = Modifier.padding(16.dp),
            state = authState
        )

        Button(onClick = { onIntent(AuthIntent.Submit) }) {
            Text(authState.authMode.title)
        }

        TextButton(onClick = { onIntent(AuthIntent.ToggleMode) }) {
            Text(authState.authMode.backButtonText)
        }
    }
}


@Composable
fun AuthInputFields(
    modifier: Modifier = Modifier,
    state: AuthState
) {
    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = modifier
            .focusProperties {
                onExit = {
                    focusRequester.requestFocus()
                }
            }
    ) {
        AuthTextField(
            labelText = "Введите логин",
            state = state.loginState,
            trailingImageVector = Icons.Default.Person
        )

        if (state.authMode.isRegister) {
            AuthTextField(
                labelText = "Введите псевдоним",
                state = state.usernameState,
                trailingImageVector = Icons.Default.Person
            )
            AuthTextField(
                labelText = "Введите почту",
                state = state.emailState,
                trailingImageVector = Icons.Default.Email
            )
        }

        AuthTextField(
            labelText = "Введите пароль",
            state = state.passwordState,
            trailingImageVector = Icons.Default.Lock,
            isSecure = true,
            isLast = !state.authMode.isRegister
        )

        if (state.authMode.isRegister) {
            AuthTextField(
                labelText = "Ещё раз введите пароль",
                state = state.confirmPasswordState,
                trailingImageVector = Icons.Default.Lock,
                isSecure = true,
                isLast = true
            )
        }
    }
}

@Composable
fun AuthTextField(
    modifier: Modifier = Modifier,
    labelText: String,
    state: TextFieldState,
    trailingImageVector: ImageVector,
    isSecure: Boolean = false,
    isLast: Boolean = false
) {
    val focusManager = LocalFocusManager.current

    OutlinedSecureTextField(
        state = state,
        modifier = modifier.fillMaxWidth(),
        label = {
            Text(labelText)
        },
        leadingIcon = { Icon(trailingImageVector, null) },
        trailingIcon = {
            if (state.text.isNotEmpty()) {
                Icon(
                    modifier = Modifier.clickable { state.clearText() },
                    imageVector = Icons.Default.Clear,
                    contentDescription = null
                )
            }
        },
        textObfuscationMode =
            if (isSecure) TextObfuscationMode.RevealLastTyped else TextObfuscationMode.Visible,
        keyboardOptions =
                if (isLast) KeyboardOptions(imeAction = ImeAction.Done)
                else KeyboardOptions(imeAction = ImeAction.Next),
        onKeyboardAction = {
            if (isLast) {
                focusManager.clearFocus()
            } else {
                focusManager.moveFocus(FocusDirection.Next)
            }
        }
    )
}