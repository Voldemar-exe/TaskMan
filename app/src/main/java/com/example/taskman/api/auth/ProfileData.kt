package com.example.taskman.api.auth

data class ProfileData(
    val token: String,
    val login: String,
    val username: String?
)
