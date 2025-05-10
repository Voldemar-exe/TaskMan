package com.example.taskman.ui.profile

sealed interface ProfileIntent {
    data object InfoClick : ProfileIntent
    data object ClearProfile : ProfileIntent
}