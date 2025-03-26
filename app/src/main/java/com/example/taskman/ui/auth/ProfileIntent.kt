package com.example.taskman.ui.auth

sealed interface ProfileIntent {
    data object InfoClick : ProfileIntent
}