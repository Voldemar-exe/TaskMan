package com.example.data.repository

import com.example.shared.ProfileData

interface SessionRepository {
    suspend fun clearSession()
    suspend fun clearProfileData()
    suspend fun getProfileData(): ProfileData?
    suspend fun updateProfileData(profileData: ProfileData)
    suspend fun saveSession(profileData: ProfileData)
    suspend fun clearDatabaseData()
}