package com.example.taskman.api.profile

class ProfileService(
    private val apiClient: ProfileApi
) {
    suspend fun deleteUserData(): Boolean {
        return try {
            val response = apiClient.deleteUserData()
            response.isSuccessful
        } catch (_: Exception) {
            false
        }
    }

    suspend fun deleteUser(): Boolean {
        return try {
            val response = apiClient.deleteUser()
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}
