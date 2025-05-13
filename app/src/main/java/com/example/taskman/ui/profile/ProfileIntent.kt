package com.example.taskman.ui.profile

sealed interface ProfileIntent {
    data object InfoClick : ProfileIntent
    data object ClearProfile : ProfileIntent
    data class UpdateUserName(val userName: String) : ProfileIntent
    data class UpdateEmail(val email: String) : ProfileIntent
    data class UpdateLogin(val login: String) : ProfileIntent
    data object SaveProfile : ProfileIntent
}