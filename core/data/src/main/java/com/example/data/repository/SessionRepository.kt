package com.example.data.repository

import com.example.data.TokenProvider
import com.example.shared.ProfileData
import kotlinx.coroutines.flow.Flow

interface SessionRepository : TokenProvider {
    suspend fun clearSession()
    suspend fun clearProfileData()
    suspend fun getProfileData(): ProfileData?
    suspend fun updateProfileData(profileData: ProfileData)
    suspend fun saveSession(profileData: ProfileData)
    suspend fun clearDatabaseData()
    fun isActiveSession(): Flow<Boolean>
}