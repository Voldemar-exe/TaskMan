package com.example.taskman

import android.app.Application
import com.example.sync.SyncWorkManagerInitializer
import dagger.hilt.android.HiltAndroidApp
import jakarta.inject.Inject

@HiltAndroidApp
class AppApplication : Application() {
    @Inject lateinit var workManagerInitializer: SyncWorkManagerInitializer

    override fun onCreate() {
        super.onCreate()
        workManagerInitializer.initialize()
    }
}
