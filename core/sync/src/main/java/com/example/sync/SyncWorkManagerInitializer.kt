package com.example.sync

import android.content.Context
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject

class SyncWorkManagerInitializer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val syncWorkerFactory: SyncWorkerFactory
) {
    fun initialize() {
        val config = Configuration.Builder()
            .setWorkerFactory(syncWorkerFactory)
            .build()

        WorkManager.initialize(context, config)
    }
}
