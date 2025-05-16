package com.example.taskman.api.profile

import android.util.Log

class ProfileService(
    private val profileClient: ProfileApi
) {

    companion object {
        private const val TAG = "ProfileService"
    }

    suspend fun deleteUserData(login: String): Boolean {
        return try {
            Log.d(TAG, "Deleting user $login data")
            val response = profileClient.deleteUserData(login)
            if (response.isSuccessful) {
                Log.d(TAG, "User $login data deleted successfully")
                true
            } else {
                Log.e(TAG, "Failed to delete user $login data: ${response.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting user $login", e)
            false
        }
    }

    suspend fun deleteUser(login: String): Boolean {
        return try {
            Log.d(TAG, "Deleting user: $login")
            val response = profileClient.deleteUser(login)
            if (response.isSuccessful) {
                Log.d(TAG, "User $login deleted successfully")
                true
            } else {
                Log.e(TAG, "Failed to delete user $login: ${response.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting user $login", e)
            false
        }
    }
}
