package com.example.data.repository

import com.example.datastore.AuthDataSource
import com.example.shared.LocalDatabase
import com.example.shared.ProfileData
import jakarta.inject.Inject


class SessionRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val db: LocalDatabase
) : SessionRepository {

    override suspend fun clearSession() {
        clearProfileData()
        clearDatabaseData()
    }

    override suspend fun clearProfileData() {
        authDataSource.clearProfile()
    }

    override suspend fun saveSession(profileData: ProfileData) {
        authDataSource.saveProfile(profileData)
    }

    override suspend fun getProfileData(): ProfileData? = authDataSource.getProfile()

    override suspend fun updateProfileData(profileData: ProfileData) {
        authDataSource.updateProfile(profileData)
    }

    override suspend fun clearDatabaseData() = db.clearDb()

    override suspend fun getToken(): String? {
        return authDataSource.getProfile()?.token
    }
}