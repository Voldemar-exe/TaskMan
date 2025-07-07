package com.example.auth

sealed interface AuthMode {
    data object Login : AuthMode
    data object Register : AuthMode
}

val AuthMode.title: String
    get() = when (this) {
        AuthMode.Login -> "Вход"
        AuthMode.Register -> "Регистрация"
    }

val AuthMode.backButtonText: String
    get() = when (this) {
        AuthMode.Login -> "Зарегистрироваться"
        AuthMode.Register -> "Уже есть аккаунт? Войти"
    }

val AuthMode.isRegister: Boolean get() = this is AuthMode.Register