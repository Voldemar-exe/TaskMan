package com.example.profile

data class ProfileState(
    val username: String = "",
    val email: String = "",
    val showInfo: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
)
