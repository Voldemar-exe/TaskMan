package com.example.taskman.ui.auth

import com.example.taskman.model.MyOption

sealed interface ProfileState {
    data object Loading : ProfileState
    data class Content(
        val userName: String,
        val options: List<MyOption>,
        val isInfo: Boolean
    ) : ProfileState
}