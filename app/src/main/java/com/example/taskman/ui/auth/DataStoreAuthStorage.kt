package com.example.taskman.ui.auth

import android.content.Context

class DataStoreAuthStorage(context: Context) : AuthStorage {


    override suspend fun saveToken(token: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getToken(): String? {
        TODO("Not yet implemented")
    }

    override suspend fun clearToken() {
        TODO("Not yet implemented")
    }
}