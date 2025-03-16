package com.example.taskman.ui.auth

import com.example.taskman.model.MyOption

sealed interface ProfileIntent {
    data object InfoClick : ProfileIntent
    data class OptionClick(val option: MyOption) : ProfileIntent
}