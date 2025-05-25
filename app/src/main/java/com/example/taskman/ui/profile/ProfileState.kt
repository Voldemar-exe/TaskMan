package com.example.taskman.ui.profile

data class ProfileState(
    val username: String = "",
    val email: String = "",
    val isInfo: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
)
