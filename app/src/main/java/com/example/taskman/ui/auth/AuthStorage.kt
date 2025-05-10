package com.example.taskman.ui.auth

import com.example.taskman.api.auth.ProfileData

interface AuthStorage {
    suspend fun saveProfile(profile: ProfileData)
    suspend fun getProfile(): ProfileData?
    suspend fun clearProfile()
}