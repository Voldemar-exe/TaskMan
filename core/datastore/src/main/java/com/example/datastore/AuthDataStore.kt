package com.example.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.shared.ProfileData
import jakarta.inject.Inject
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import java.io.IOException

private val Context.authDataStore by preferencesDataStore(
    name = "auth_prefs"
)

class AuthDataStore @Inject constructor(context: Context) : AuthDataSource {

    private val TOKEN = stringPreferencesKey("jwt")
    private val USERNAME = stringPreferencesKey("user_name")
    private val EMAIL = stringPreferencesKey("user_email")

    private val dataStore = context.authDataStore

    companion object {
        private const val TAG = "AuthDataStore"
    }

    override suspend fun saveProfile(profileData: ProfileData) {
        dataStore.edit {
            it[TOKEN] = profileData.token
            it[EMAIL] = profileData.email
            it[USERNAME] = profileData.username
        }
        Log.d(
            TAG,
            "Saved profile → token=${profileData.token.take(5)}...," +
                    " email=${profileData.email}, username=${profileData.username}"
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
        val email = prefs[EMAIL]
        val username = prefs[USERNAME]
        val profile =
            if (!token.isNullOrBlank() && !email.isNullOrBlank() && !username.isNullOrBlank()) {
                ProfileData(token, username, email)
            } else {
                null
            }

        if (profile != null) {
            Log.d(
                TAG,
                "Loaded profile → token=${profile.token.take(5)}...," +
                        " email=${profile.email}, username=${profile.username}"
            )
        } else {
            Log.d(TAG, "No profile found in DataStore")
        }

        return profile
    }

    override suspend fun updateProfile(profileData: ProfileData) {
        dataStore.edit { prefs ->
            prefs[TOKEN] = profileData.token.ifBlank { prefs[TOKEN] ?: "" }
            prefs[EMAIL] = profileData.email.ifBlank { prefs[EMAIL] ?: "" }
            prefs[USERNAME] = profileData.username.ifBlank { prefs[USERNAME] ?: "" }
        }
    }

    override suspend fun clearProfile() {
        dataStore.edit { it.clear() }
    }
}
