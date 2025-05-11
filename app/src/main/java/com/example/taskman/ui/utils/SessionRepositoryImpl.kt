package com.example.taskman.ui.utils

import com.example.taskman.api.auth.ProfileData
import com.example.taskman.db.TaskManDatabase
import com.example.taskman.ui.auth.AuthStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SessionRepositoryImpl(
    private val authStorage: AuthStorage,
    private val db: TaskManDatabase
) : SessionRepository {

    override suspend fun clearSession() {
        authStorage.clearProfile()

        withContext(Dispatchers.IO) {
            db.clearAllTables()
        }
    }

    override suspend fun saveSession(profileData: ProfileData) {
        authStorage.saveProfile(profileData)
    }
}