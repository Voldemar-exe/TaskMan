package com.example.taskman.api.auth

data class ProfileData(
    val token: String,
    val username: String,
    val email: String
)
