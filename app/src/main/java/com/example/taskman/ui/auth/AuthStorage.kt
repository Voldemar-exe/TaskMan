package com.example.taskman.ui.auth

import com.example.taskman.api.auth.ProfileData

interface AuthStorage {
    suspend fun saveProfile(profileData: ProfileData)
    suspend fun getProfile(): ProfileData?
    suspend fun updateProfile(profileData: ProfileData)
    suspend fun clearProfile()
}
