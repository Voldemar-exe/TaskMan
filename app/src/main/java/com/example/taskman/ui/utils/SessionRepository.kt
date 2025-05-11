package com.example.taskman.ui.utils

import com.example.taskman.api.auth.ProfileData

interface SessionRepository {
    suspend fun clearSession()
    suspend fun clearProfileData()
    suspend fun clearDatabaseData()
    suspend fun saveSession(profileData: ProfileData)
}