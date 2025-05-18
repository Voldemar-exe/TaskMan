package com.example.taskman

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import com.example.taskman.api.sync.SyncService
import com.example.taskman.api.sync.SyncWorkerFactory
import com.example.taskman.db.TaskManDatabase
import com.example.taskman.di.dataModule
import com.example.taskman.di.domainModule
import com.example.taskman.di.presentationModule
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class AppApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@AppApplication)
            modules(
                dataModule,
                domainModule,
                presentationModule
            )
        }

        WorkManager.initialize(
            this,
            Configuration.Builder()
                .setWorkerFactory(
                    SyncWorkerFactory(
                        inject<TaskManDatabase>().value,
                        inject<SyncService>().value
                    )
                ).build()
        )
    }
}
