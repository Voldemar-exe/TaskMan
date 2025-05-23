package com.example.taskman.ui.profile

import com.example.taskman.model.MyOption

data class ProfileState(
    val username: String = "",
    val email: String = "",
    val options: List<MyOption> = emptyList(),
    val isInfo: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
)
