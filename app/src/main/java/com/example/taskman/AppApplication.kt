package com.example.taskman

import android.app.Application
import com.example.taskman.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class AppApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@AppApplication)
            modules(appModule)
        }
    }
}