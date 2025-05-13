package com.example.taskman.ui.auth

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.taskman.api.auth.ProfileData
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import java.io.IOException

private val Context.authDataStore by preferencesDataStore(
    name = "auth_prefs"
)

class AuthDataStore(context: Context) : AuthStorage {

    private val TOKEN = stringPreferencesKey("jwt")
    private val LOGIN = stringPreferencesKey("user_login")
    private val USERNAME = stringPreferencesKey("user_name")

    private val dataStore = context.authDataStore

    companion object {
        private const val TAG = "AuthDataStore"
    }

    override suspend fun saveProfile(profileData: ProfileData) {
        dataStore.edit {
            it[TOKEN] = profileData.token
            it[LOGIN] = profileData.login
            it[USERNAME] = profileData.username ?: ""
        }
        Log.d(
            TAG,
            "Saved profile → token=${profileData.token.take(5)}...," +
                    " login=${profileData.login}, username=${profileData.username}"
        )
    }

    override suspend fun getProfile(): ProfileData? {
        val prefs = dataStore.data
            .catch { e ->
                Log.e(TAG, "Error reading preferences, emitting empty", e)
                if (e is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw e
                }
            }
            .first()

        val token = prefs[TOKEN]
        val login = prefs[LOGIN]
        val username = prefs[USERNAME]

        val profile = if (!token.isNullOrBlank() && !login.isNullOrBlank()) {
            ProfileData(token, login, username)
        } else {
            null
        }

        if (profile != null) {
            Log.d(
                TAG,
                "Loaded profile → token=${profile.token.take(5)}...," +
                        " login=${profile.login}, username=${profile.username}"
            )
        } else {
            Log.d(TAG, "No profile found in DataStore")
        }

        return profile
    }

    override suspend fun updateProfile(profileData: ProfileData) {
        dataStore.edit { prefs ->
            prefs[TOKEN] = profileData.token.ifBlank { prefs[TOKEN] ?: "" }
            prefs[LOGIN] = profileData.login.ifBlank { prefs[LOGIN] ?: "" }
            prefs[USERNAME] = profileData.username?.ifBlank {
                prefs[USERNAME] ?: ""
            } ?: prefs[USERNAME] ?: ""
        }
    }

    override suspend fun clearProfile() {
        dataStore.edit { it.clear() }
    }
}
