package com.example.taskman.di

import androidx.work.WorkManager
import com.example.taskman.api.auth.AuthService
import com.example.taskman.api.profile.ProfileService
import com.example.taskman.api.sync.SyncService
import com.example.taskman.ui.utils.SessionRepository
import org.koin.dsl.module


val domainModule = module {
    single<AuthService> { AuthService(get(), get<SessionRepository>()) }
    single<ProfileService> { ProfileService(get()) }
    single<SyncService> { SyncService(get()) }

    single<WorkManager> { WorkManager.getInstance(get()) }
}