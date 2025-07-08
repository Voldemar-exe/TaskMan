package com.example.datastore

import com.example.shared.ProfileData
import kotlinx.coroutines.flow.Flow


interface AuthDataSource {
    suspend fun saveProfile(profileData: ProfileData)
    suspend fun getProfile(): ProfileData?
    suspend fun updateProfile(profileData: ProfileData)
    suspend fun clearProfile()
    fun isActiveSession(): Flow<Boolean>
}
