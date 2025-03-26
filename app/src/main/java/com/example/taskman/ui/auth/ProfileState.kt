package com.example.taskman.ui.auth

import com.example.taskman.model.MyOption

data class ProfileState(
    val userName: String = "",
    val options: List<MyOption> = emptyList(),
    val isInfo: Boolean = false
)